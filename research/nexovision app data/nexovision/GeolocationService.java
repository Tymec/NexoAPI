package eu.nexwell.android.nexovision;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import nexovision.android.nexwell.eu.nexovision.R;

public class GeolocationService extends Service {
    private static String TAG = "GeolocationService";
    public static Context context;
    private static Location current_location;
    private static GeolocationPoint glp_updated;
    public static Handler handler;
    public static boolean isRunning = false;
    private static Semaphore listenSem = new Semaphore(1);
    private static ArrayList<GeolocationListener> listeners;
    private static LocationListener locationListener;
    private static LocationManager locationManager;
    public static Ringtone ringtone;

    /* renamed from: eu.nexwell.android.nexovision.GeolocationService$1 */
    class C20071 implements Runnable {
        C20071() {
        }

        public void run() {
            GeolocationService.playSound();
        }
    }

    public interface GeolocationListener {
        void onLocationUpdate(Location location);

        void onStateChange(GeolocationPoint geolocationPoint, float f);

        void onStateUpdate(GeolocationPoint geolocationPoint, float f);
    }

    private class MyLocationListener implements LocationListener {
        private static final long TWO_MINUTES = 120000;
        private Location bestLocation;

        private MyLocationListener() {
        }

        public void onLocationChanged(final Location loc) {
            Log.d("GeoService", "Latitude: " + loc.getLatitude());
            Log.d("GeoService", "Longitude: " + loc.getLongitude());
            Log.d("GeoService", "Accuracy: " + loc.getAccuracy());
            makeUseOfNewLocation(loc);
            GeolocationService.handler.post(new Runnable() {
                public void run() {
                    if (loc != null) {
                        Calendar.getInstance().setTimeInMillis(loc.getTime());
                        GeolocationService.this.notifyListeners(loc);
                    }
                    if (MyLocationListener.this.bestLocation != null) {
                        GeolocationService.current_location = MyLocationListener.this.bestLocation;
                    }
                    if (GeolocationService.current_location != null) {
                        GeolocationService.this.checkDistance(GeolocationService.current_location);
                    }
                }
            });
        }

        private void makeUseOfNewLocation(Location location) {
            if (isBetterLocation(location, this.bestLocation)) {
                this.bestLocation = location;
            }
        }

        protected boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null || location == null) {
                return true;
            }
            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -120000;
            boolean isNewer = timeDelta > 0;
            if (isSignificantlyNewer) {
                return true;
            }
            if (isSignificantlyOlder) {
                return false;
            }
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;
            boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
            if (isMoreAccurate) {
                return true;
            }
            if (isNewer && !isLessAccurate) {
                return true;
            }
            if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return true;
            }
            return false;
        }

        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            } else {
                return provider1.equals(provider2);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    static {
        listeners = null;
        listeners = new ArrayList();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        context = this;
    }

    public void onDestroy() {
        Log.d(TAG, "STOP");
        stopForeground(true);
        locationManager.removeUpdates(locationListener);
        stopRingtone();
        isRunning = false;
        super.onDestroy();
    }

    public void onStart(Intent intent, int startid) {
        isRunning = true;
        handler = new Handler();
        Log.d(TAG, "START");
        setupGeolocation();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        isRunning = true;
        handler = new Handler();
        Log.d(TAG, "START");
        setupGeolocation();
        return 1;
    }

    private void showNotification() {
        Notification foregroundNotification;
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 536870912);
        if (VERSION.SDK_INT < 21) {
            foregroundNotification = new Builder(getApplicationContext()).setContentTitle("NexoVision").setContentText("GeoLocation Service is running").setSmallIcon(R.drawable.logo_nexovision).setWhen(System.currentTimeMillis()).build();
        } else {
            foregroundNotification = new Builder(getApplicationContext()).setContentTitle("NexoVision").setContentText("GeoLocation Service is running").setSmallIcon(R.drawable.logo_nexovision).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.favicon)).setWhen(System.currentTimeMillis()).build();
        }
        startForeground(7, foregroundNotification);
    }

    private void setupGeolocation() {
        locationManager = (LocationManager) getSystemService(Param.LOCATION);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates("gps", 3000, 5.0f, locationListener);
        locationManager.requestLocationUpdates("network", 3000, 5.0f, locationListener);
        current_location = getLastBestLocation();
        if (current_location != null) {
            checkDistance(current_location);
            Calendar.getInstance().setTimeInMillis(current_location.getTime());
            notifyListeners(current_location);
        }
    }

    public static Location getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation()");
        if (context != null) {
            current_location = ((GeolocationService) context).getLastBestLocation();
        }
        return current_location;
    }

    private Location getLastBestLocation() {
        Location currentlocation_gps = locationManager.getLastKnownLocation("gps");
        Location currentlocation_net = locationManager.getLastKnownLocation("network");
        long GPSLocationTime = 0;
        if (currentlocation_gps != null) {
            GPSLocationTime = currentlocation_gps.getTime();
        }
        long NetLocationTime = 0;
        if (currentlocation_net != null) {
            NetLocationTime = currentlocation_net.getTime();
        }
        return 0 < GPSLocationTime - NetLocationTime ? currentlocation_gps : currentlocation_net;
    }

    private void checkDistance(Location loc) {
        Iterator<IElement> itrg = NVModel.getElementsByType(NVModel.EL_TYPE_GEOLOCATIONPOINT).iterator();
        while (itrg.hasNext()) {
            IElement el = (IElement) itrg.next();
            if (el != null && (el instanceof GeolocationPoint)) {
                GeolocationPoint glp = (GeolocationPoint) el;
                Location target_location = glp.getLocation();
                if (target_location != null) {
                    float distance = loc.distanceTo(target_location);
                    float total_accuracy = loc.getAccuracy() + target_location.getAccuracy();
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
                            notifyListenersAbtStateChange(glp, distance);
                        }
                    } else if (mode == -1 && distance > ((float) radius) + total_accuracy) {
                        mode = 1;
                        glp.setState(GeolocationPoint.GEOLOCP_STATE_OUT);
                        glp.setDistance(distance);
                        notifyListenersAbtStateChange(glp, distance);
                    }
                    if (mode == 1) {
                        glp.setState(GeolocationPoint.GEOLOCP_STATE_OUT);
                        glp.setDistance(distance);
                    } else {
                        glp.setState(GeolocationPoint.GEOLOCP_STATE_IN);
                        glp.setDistance(distance);
                    }
                    glp.setMode(mode);
                    notifyListenersAbtStateUpdate(glp, distance);
                }
            }
        }
    }

    public static void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    public static void playSound() {
        Uri alarm = RingtoneManager.getDefaultUri(2);
        if (alarm != null) {
            ringtone = RingtoneManager.getRingtone(context, alarm);
            if (ringtone != null && !ringtone.isPlaying()) {
                ringtone.play();
            }
        }
    }

    public static void stopSound() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    public static void addGeolocationListener(GeolocationListener l) {
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

    public static void removeGeolocationListener(GeolocationListener l) {
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
                    GeolocationListener listener = (GeolocationListener) listeners.get(i);
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

    private void notifyListenersAbtStateChange(GeolocationPoint glp, float distance) {
        handler.post(new C20071());
        if (MainActivity.VISIBLE) {
            try {
                listenSem.acquire();
                if (listeners != null) {
                    for (int i = 0; i < listeners.size(); i++) {
                        GeolocationListener listener = (GeolocationListener) listeners.get(i);
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
        glp_updated = glp;
        Intent intent = new Intent().setClass(context, MainActivity.class);
        intent.addFlags(DriveFile.MODE_READ_ONLY);
        startActivity(intent);
    }

    private void notifyListenersAbtStateUpdate(GeolocationPoint glp, float distance) {
        try {
            listenSem.acquire();
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    GeolocationListener listener = (GeolocationListener) listeners.get(i);
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

    public static GeolocationPoint getLastUpdatedGlp() {
        GeolocationPoint tmp = glp_updated;
        glp_updated = null;
        return tmp;
    }
}
