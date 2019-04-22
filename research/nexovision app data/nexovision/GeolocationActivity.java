package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.Calendar;
import nexovision.android.nexwell.eu.nexovision.R;

public class GeolocationActivity extends AppCompatActivity {
    private static Context context;
    private static Location current_location;
    private static float distance;
    private static FloatingActionButton fab;
    private static Handler handler;
    private static LocationListener locationListener;
    private static LocationManager locationManager;
    private static int mode = 0;
    private static int radius;
    private static AlertDialog targetRadiusInputDialog;
    private static Location target_location;
    private String TAG = "GeolocationActivity";
    private TextView currentLocationTextView;
    private TextView distanceTextView;
    private TextView statusTextView;
    private TextView targetLocationTextView;

    /* renamed from: eu.nexwell.android.nexovision.GeolocationActivity$1 */
    class C20011 implements OnFocusChangeListener {
        C20011() {
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                GeolocationActivity.targetRadiusInputDialog.getWindow().setSoftInputMode(5);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GeolocationActivity$3 */
    class C20033 implements OnClickListener {
        C20033() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GeolocationActivity$4 */
    class C20044 implements View.OnClickListener {
        C20044() {
        }

        public void onClick(View view) {
            GeolocationActivity.targetRadiusInputDialog.show();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.GeolocationActivity$5 */
    class C20055 implements Runnable {
        C20055() {
        }

        public void run() {
            GeolocationActivity.locationManager = (LocationManager) GeolocationActivity.this.getSystemService(Param.LOCATION);
            GeolocationActivity.locationListener = new MyLocationListener();
            GeolocationActivity.locationManager.requestLocationUpdates("gps", 3000, 5.0f, GeolocationActivity.locationListener);
            GeolocationActivity.locationManager.requestLocationUpdates("network", 3000, 5.0f, GeolocationActivity.locationListener);
            GeolocationActivity.current_location = GeolocationActivity.this.getLastBestLocation();
            GeolocationActivity.this.updateLocation(GeolocationActivity.current_location, GeolocationActivity.this.currentLocationTextView);
            GeolocationActivity.this.updateDistance(GeolocationActivity.current_location);
            if (GeolocationActivity.current_location != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(GeolocationActivity.current_location.getTime());
                GeolocationActivity.this.statusTextView.setText("Status: location changed (time: " + calendar.get(11) + ":" + calendar.get(12) + ":" + calendar.get(13) + ")");
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        private static final long TWO_MINUTES = 120000;
        private Location bestLocation;

        private MyLocationListener() {
        }

        public void onLocationChanged(final Location loc) {
            makeUseOfNewLocation(loc);
            GeolocationActivity.handler.post(new Runnable() {
                public void run() {
                    if (loc != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(loc.getTime());
                        GeolocationActivity.this.statusTextView.setText("Status: location changed (time: " + calendar.get(11) + ":" + calendar.get(12) + ":" + calendar.get(13) + ")");
                    }
                    GeolocationActivity.current_location = MyLocationListener.this.bestLocation;
                    GeolocationActivity.this.updateLocation(GeolocationActivity.current_location, GeolocationActivity.this.currentLocationTextView);
                    GeolocationActivity.this.updateDistance(GeolocationActivity.current_location);
                }
            });
        }

        private void makeUseOfNewLocation(Location location) {
            if (isBetterLocation(location, this.bestLocation)) {
                this.bestLocation = location;
            }
        }

        protected boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
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

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_geolocalization);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.statusTextView = (TextView) findViewById(R.id.status);
        this.currentLocationTextView = (TextView) findViewById(R.id.current_location);
        this.distanceTextView = (TextView) findViewById(R.id.distance);
        this.targetLocationTextView = (TextView) findViewById(R.id.target_location);
        this.statusTextView.setText("Status: waiting...");
        this.distanceTextView.setText("Distance: no target");
        this.currentLocationTextView.setText("no signal");
        this.targetLocationTextView.setText("not set");
        Builder builder = new Builder(context);
        builder.setTitle(context.getString(R.string.GeolocationActivity_AddTarget_DialogTitle));
        final EditText targetRadiusInput = new EditText(context);
        targetRadiusInput.setInputType(2);
        targetRadiusInput.setGravity(17);
        targetRadiusInput.setOnFocusChangeListener(new C20011());
        builder.setView(targetRadiusInput);
        builder.setPositiveButton(context.getString(R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String r = targetRadiusInput.getText().toString();
                if (r != null && !r.isEmpty()) {
                    GeolocationActivity.radius = Integer.parseInt(r);
                    if (GeolocationActivity.current_location != null) {
                        GeolocationActivity.target_location = GeolocationActivity.current_location;
                        GeolocationActivity.this.updateLocation(GeolocationActivity.target_location, GeolocationActivity.this.targetLocationTextView);
                        GeolocationActivity.this.targetLocationTextView.append("\nRadius: " + GeolocationActivity.radius + " m");
                        return;
                    }
                    MainActivity.displayInfo(GeolocationActivity.context, "No current location");
                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.CANCEL), new C20033());
        targetRadiusInputDialog = builder.create();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C20044());
        }
    }

    protected void onStart() {
        super.onStart();
        handler.post(new C20055());
    }

    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
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

    private void updateLocation(Location loc, TextView locationTextView) {
        if (loc == null) {
            locationTextView.setText("no info");
            return;
        }
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(this.TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(this.TAG, latitude);
        String accuracy = "Accuracy: " + loc.getAccuracy() + "m";
        Log.v(this.TAG, accuracy);
        locationTextView.setText(longitude + "\n" + latitude + "\n" + accuracy);
    }

    private void updateDistance(Location loc) {
        if (target_location != null) {
            distance = loc.distanceTo(target_location);
            float total_accuracy = loc.getAccuracy() + target_location.getAccuracy();
            String state = "?";
            if (mode == 0) {
                if (distance > ((float) radius)) {
                    mode = 1;
                } else {
                    mode = -1;
                }
            } else if (mode == 1) {
                if (distance < ((float) radius) - total_accuracy) {
                    mode = -1;
                    MainActivity.displayInfo(context, "You entered the area. You are IN now.");
                }
            } else if (mode == -1 && distance > ((float) radius) + total_accuracy) {
                mode = 1;
                MainActivity.displayInfo(context, "You left out the area. You are OUT now.");
            }
            if (mode == 1) {
                state = "you are OUT";
            } else {
                state = "you are IN";
            }
            this.distanceTextView.setText("DISTANCE: " + String.format("%.1f", new Object[]{Float.valueOf(distance)}) + " (+/- " + String.format("%.1f", new Object[]{Float.valueOf(loc.getAccuracy())}) + ") m\nState: " + state);
            return;
        }
        this.distanceTextView.setText("DISTANCE: no target");
    }
}
