package eu.nexwell.android.nexovision;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import nexovision.android.nexwell.eu.nexovision.R;

public class GeoService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    static int LOCATION_REQUEST_INTERVAL_MSEC = 1000;
    static float LOCATION_REQUEST_SMALLEST_DISPLACEMENT = 10.0f;
    private static final String TAG = "GeoService";
    private static final int TIME_DIFFERENCE_THRESHOLD = 20000;
    public static boolean isMainActivityRunning = false;
    private static Semaphore listenSem = new Semaphore(1);
    private static ArrayList<GeoListener> listeners = new ArrayList();
    private boolean checkIfUpdated;
    private Handler handler;
    private ArrayList<IElement> list;
    private Location mBestLocation;
    private ArrayList<IElement> mGeoLocationPointsList;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    public interface GeoListener {
        void onLocationUpdate(Location location);

        void onStateChange(GeolocationPoint geolocationPoint, float f);

        void onStateUpdate(GeolocationPoint geolocationPoint, float f);
    }

    public void onCreate() {
        super.onCreate();
        this.mGeoLocationPointsList = (ArrayList) NVModel.getElementsByType(NVModel.EL_TYPE_GEOLOCATIONPOINT).clone();
        this.mGoogleApiClient = new Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        this.mLastLocation = null;
        this.mBestLocation = null;
        this.handler = new Handler();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GeoService started");
        showNotification();
        this.mGoogleApiClient.connect();
        return 1;
    }

    private void showNotification() {
        Notification foregroundNotification;
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 536870912);
        if (VERSION.SDK_INT < 21) {
            foregroundNotification = new Notification.Builder(getApplicationContext()).setContentTitle(getResources().getString(R.string.APP_NAME)).setContentText(getResources().getString(R.string.GeolocationService_is_ON)).setSmallIcon(R.drawable.favicon).setWhen(System.currentTimeMillis()).build();
        } else {
            foregroundNotification = new Notification.Builder(getApplicationContext()).setContentTitle(getResources().getString(R.string.APP_NAME)).setContentText(getResources().getString(R.string.GeolocationService_is_ON)).setSmallIcon(R.drawable.favicon).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.favicon)).setWhen(System.currentTimeMillis()).build();
        }
        startForeground(7, foregroundNotification);
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Log.d(TAG, "GeoService finished");
        stopForeground(true);
        this.mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public static boolean isLocationEnabled(Context context) {
        if (VERSION.SDK_INT >= 19) {
            try {
                if (Secure.getInt(context.getContentResolver(), "location_mode") != 0) {
                    return true;
                }
                return false;
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else if (TextUtils.isEmpty(Secure.getString(context.getContentResolver(), "location_providers_allowed"))) {
            return false;
        } else {
            return true;
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval((long) LOCATION_REQUEST_INTERVAL_MSEC);
        this.mLocationRequest.setFastestInterval((long) LOCATION_REQUEST_INTERVAL_MSEC);
        this.mLocationRequest.setPriority(100);
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
        }
    }

    public void onConnectionSuspended(int i) {
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "Accuracy: " + location.getAccuracy());
        try {
            this.list = NVModel.getElementsByType(NVModel.EL_TYPE_GEOLOCATIONPOINT);
            this.checkIfUpdated = true;
        } catch (Exception e) {
            this.checkIfUpdated = false;
        }
        if (this.checkIfUpdated) {
            checkIfNeedUpdateGeoLocationPoints(this.list, this.mGeoLocationPointsList);
        }
        if (isBetterLocation(this.mBestLocation, location)) {
            this.mBestLocation = location;
            notifyListeners(this.mBestLocation);
            checkRadius(this.mBestLocation);
        }
    }

    private boolean isBetterLocation(Location oldLocation, Location newLocation) {
        if (oldLocation == null) {
            return true;
        }
        boolean isMoreAccurate;
        boolean isNewer;
        if (newLocation.getTime() > oldLocation.getTime()) {
            isNewer = true;
        } else {
            isNewer = false;
        }
        if (newLocation.getAccuracy() <= oldLocation.getAccuracy()) {
            isMoreAccurate = true;
        } else {
            isMoreAccurate = false;
        }
        if (isMoreAccurate && isNewer) {
            return true;
        }
        if (!isMoreAccurate || isNewer) {
            if (newLocation.distanceTo(oldLocation) > newLocation.getAccuracy()) {
                return true;
            }
        } else if (newLocation.getTime() - oldLocation.getTime() > -20000) {
            return true;
        }
        return false;
    }

    private void checkRadius(Location location) {
        Iterator<IElement> itrg = this.mGeoLocationPointsList.iterator();
        while (itrg.hasNext()) {
            IElement el = (IElement) itrg.next();
            if (el != null && (el instanceof GeolocationPoint)) {
                GeolocationPoint glp = (GeolocationPoint) el;
                Location target_location = glp.getLocation();
                if (target_location != null) {
                    float distance = location.distanceTo(target_location);
                    float total_accuracy = location.getAccuracy() + target_location.getAccuracy();
                    Log.d(TAG, "GLP: " + glp.getName() + ", distance: " + distance);
                    int mode = glp.getMode();
                    int radius = glp.getRadius();
                    if (mode == 0) {
                        mode = distance > ((float) radius) ? 1 : -1;
                    } else if (mode == 1) {
                        if (distance < ((float) radius) - total_accuracy) {
                            mode = -1;
                            glp.setState(GeolocationPoint.GEOLOCP_STATE_IN);
                            glp.setDistance(distance);
                            notifyListenersStateChange(glp, distance);
                            Log.d(TAG, "You entered the area. You are IN now.");
                        }
                    } else if (mode == -1 && distance > ((float) radius) + total_accuracy) {
                        mode = 1;
                        glp.setState(GeolocationPoint.GEOLOCP_STATE_OUT);
                        glp.setDistance(distance);
                        Log.d(TAG, "You left out the area. You are OUT now.");
                        notifyListenersStateChange(glp, distance);
                    }
                    if (mode == 1) {
                        glp.setState(GeolocationPoint.GEOLOCP_STATE_OUT);
                        glp.setDistance(distance);
                    } else {
                        glp.setState(GeolocationPoint.GEOLOCP_STATE_IN);
                        glp.setDistance(distance);
                    }
                    glp.setMode(mode);
                    notifyListenersStateUpdate(glp, distance);
                }
            }
        }
    }

    public static void addGeoListener(GeoListener l) {
        try {
            listenSem.acquire();
            listeners.add(l);
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    public static void removeGeoListener(GeoListener l) {
        try {
            listenSem.acquire();
            listeners.remove(l);
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    private void notifyListeners(Location loc) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    GeoListener listener = (GeoListener) listeners.get(i);
                    if (listener != null) {
                        listener.onLocationUpdate(loc);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    private void notifyListenersStateChange(GeolocationPoint glp, float distance) {
        Log.d(TAG, "notifyListenersStateChange method");
        if (isMainActivityRunning) {
            try {
                listenSem.acquire();
                if (listeners != null) {
                    for (int i = 0; i < listeners.size(); i++) {
                        GeoListener listener = (GeoListener) listeners.get(i);
                        if (listener != null) {
                            listener.onStateChange(glp, distance);
                        }
                    }
                }
                listenSem.release();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Throwable th) {
                listenSem.release();
            }
        }
        Intent intent = new Intent().setClass(getApplicationContext(), MainActivity.class);
        intent.addFlags(DriveFile.MODE_READ_ONLY);
        intent.putExtra("showGPSDialog", true);
        intent.putExtra("geolocationpointid", glp.getId());
        startActivity(intent);
    }

    private void notifyListenersStateUpdate(GeolocationPoint glp, float distance) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    GeoListener listener = (GeoListener) listeners.get(i);
                    if (listener != null) {
                        listener.onStateUpdate(glp, distance);
                    }
                }
            }
            listenSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            listenSem.release();
        }
    }

    private void checkIfNeedUpdateGeoLocationPoints(ArrayList<IElement> from, ArrayList<IElement> toUpdate) {
        if (from.size() != toUpdate.size()) {
            toUpdate = from;
        } else if (!toUpdate.equals(from)) {
            toUpdate = from;
        }
    }
}
