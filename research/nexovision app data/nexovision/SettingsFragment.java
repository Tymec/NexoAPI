package eu.nexwell.android.nexovision;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.drive.DriveFile;
import eu.nexwell.android.nexovision.communication.CommunicationException;
import nexovision.android.nexwell.eu.nexovision.R;
import org.linphone.mini.VoipRRService;

public class SettingsFragment extends PreferenceFragment {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "GeoService";
    private Context _context;
    private CheckBoxPreference geolocationservice_on;
    private OnSharedPreferenceChangeListener listener;
    private WifiManager wifiManager;

    /* renamed from: eu.nexwell.android.nexovision.SettingsFragment$1 */
    class C20871 implements OnPreferenceChangeListener {
        C20871() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (MainActivity.getContext().getResources().getConfiguration().locale.getLanguage().equals((String) newValue)) {
                preference.setSummary(null);
            } else {
                preference.setSummary(MainActivity.getContext().getString(R.string.PreferencesActivity_GlobalCat_Language_Summary));
            }
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.SettingsFragment$2 */
    class C20882 implements OnSharedPreferenceChangeListener {
        C20882() {
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_devicenumber")) {
                String value = sharedPreferences.getString(key, null);
                if (value != null && !value.equals("") && VoipRRService.communication.isConnected()) {
                    try {
                        VoipRRService.send("GET /?mobileIdentify&mobilephone=" + value + "&netaddress=" + VoipRRService.communication.getIpAddress());
                    } catch (CommunicationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.SettingsFragment$3 */
    class C20893 implements OnPreferenceChangeListener {
        C20893() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (newValue instanceof Boolean) {
                if (VERSION.SDK_INT >= 23) {
                    SettingsFragment.this.checkLocationPermission();
                }
                if (((Boolean) newValue).booleanValue()) {
                    SettingsFragment.this.getActivity().startService(new Intent(SettingsFragment.this.getActivity(), GeoService.class));
                    if (!(GeoService.isLocationEnabled(SettingsFragment.this._context) && SettingsFragment.this.wifiManager.isWifiEnabled())) {
                        SettingsFragment.this.showNotRunningGPSDialog();
                    }
                } else {
                    SettingsFragment.this.getActivity().stopService(new Intent(SettingsFragment.this.getActivity(), GeoService.class));
                }
            }
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.SettingsFragment$4 */
    class C20904 implements OnClickListener {
        C20904() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
            intent.addFlags(DriveFile.MODE_READ_ONLY);
            SettingsFragment.this._context.startActivity(intent);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.SettingsFragment$5 */
    class C20915 implements OnClickListener {
        C20915() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
            intent.addFlags(DriveFile.MODE_READ_ONLY);
            SettingsFragment.this._context.startActivity(intent);
            if (!SettingsFragment.this.wifiManager.isWifiEnabled()) {
                SettingsFragment.this.wifiManager.setWifiEnabled(true);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.SettingsFragment$6 */
    class C20926 implements OnClickListener {
        C20926() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        this.wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService("wifi");
        this._context = getActivity();
        DynamicListPreferenceWithCurrentEntry lang = (DynamicListPreferenceWithCurrentEntry) findPreference("pref_language");
        lang.setSummary(null);
        lang.setEntryValues((CharSequence[]) MainActivity.LanguageCodeList.toArray(new CharSequence[MainActivity.LanguageCodeList.size()]));
        lang.setEntries((CharSequence[]) MainActivity.LanguageList.toArray(new CharSequence[MainActivity.LanguageList.size()]));
        lang.setValue(PreferenceManager.getDefaultSharedPreferences(this._context).getString(lang.getKey(), (String) MainActivity.LanguageCodeList.get(0)));
        lang.setOnPreferenceChangeListener(new C20871());
        this.listener = new C20882();
        this.geolocationservice_on = (CheckBoxPreference) findPreference("pref_geolocationservice_on");
        this.geolocationservice_on.setChecked(isMyServiceRunning(GeoService.class));
        this.geolocationservice_on.setOnPreferenceChangeListener(new C20893());
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this.listener);
        Preference version = findPreference("pref_version");
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            Log.d("SettingsFragment", "version=" + pInfo.versionName);
            version.setSummary(pInfo.versionName);
        } catch (NameNotFoundException e) {
            version.setSummary("...");
            e.printStackTrace();
        }
    }

    public void onDetach() {
        super.onDetach();
        PreferenceManager.getDefaultSharedPreferences(this._context).unregisterOnSharedPreferenceChangeListener(this.listener);
        this.listener = null;
    }

    private void showNotRunningGPSDialog() {
        Log.d(TAG, "GPS Dialog - not running provider");
        Builder alertGPSDialog = new Builder(this._context);
        alertGPSDialog.setTitle(getResources().getString(R.string.GPS_PROVIDER_TITLE));
        alertGPSDialog.setMessage(getResources().getString(R.string.GPS_PROVIDER_QUESTION));
        alertGPSDialog.setCancelable(false);
        alertGPSDialog.setPositiveButton(getResources().getString(R.string.YES), new C20904());
        alertGPSDialog.setNeutralButton(getResources().getString(R.string.YESwithWIFI), new C20915());
        alertGPSDialog.setNegativeButton(getResources().getString(R.string.NO), new C20926());
        alertGPSDialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        for (RunningServiceInfo service : ((ActivityManager) getActivity().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = 23)
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.ACCESS_FINE_LOCATION") == 0) {
            return true;
        }
        if (shouldShowRequestPermissionRationale("android.permission.ACCESS_FINE_LOCATION")) {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 99);
            return false;
        }
        requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 99);
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 99:
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    Log.d(TAG, "Denied");
                    this.geolocationservice_on.setChecked(false);
                    Toast.makeText(getContext(), getResources().getString(R.string.PERMISSION_DENIED), 1).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    public Context getContext() {
        return this._context;
    }
}
