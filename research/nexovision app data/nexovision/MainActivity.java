package eu.nexwell.android.nexovision;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import eu.nexwell.android.nexovision.GeoService.GeoListener;
import eu.nexwell.android.nexovision.communication.CommunicationException;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.misc.XMLProject;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.Dimmer;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.Light;
import eu.nexwell.android.nexovision.model.LightGroup;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Polygon;
import eu.nexwell.android.nexovision.model.RGBW;
import eu.nexwell.android.nexovision.model.Scene;
import eu.nexwell.android.nexovision.model.Switch;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import nexovision.android.nexwell.eu.nexovision.R;
import org.linphone.mini.AlarmReceiver;
import org.linphone.mini.VidSockets;

public class MainActivity extends AppCompatActivity implements NexoTalkListener, GeoListener {
    public static int CURR_SET_ID = 0;
    public static Intent GeolocationServiceIntent = null;
    public static Intent InBkgListenerServiceIntent = null;
    public static ArrayList<String> LanguageCodeList = new ArrayList();
    public static ArrayList<String> LanguageList = new ArrayList();
    private static final int REQUEST_CODE_MULTIPLE_PERMISSIONS = 666;
    public static int[] RGBWColors = new int[8];
    public static boolean VISIBLE = false;
    private static ProgressDialog connectionDialog;
    private static Context connectionDialogContext;
    private static boolean connectionStatus;
    private static Context context;
    private static AlertDialog devNumberInputDialog;
    private static AlertDialog exitDialog;
    public static LinearLayout fragment;
    public static Handler handler;
    private static String lastDisplayedError = "";
    public static GridLayout mainGrid;
    private static Menu menu;
    private static AlertDialog sceneNameInputDialog;
    private static SharedPreferences sharedPrefs;
    AlarmReceiver alarm = new AlarmReceiver();
    private Editor editor;
    private AudioManager mAudioManager;
    private ComponentName mReceiverComponent;
    private ViewTreeObserver vto;

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$1 */
    class C20251 implements OnClickListener {
        C20251() {
        }

        public void onClick(View v) {
            MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_ALARM).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_ALARM).getName());
            if (!MainActivity.isLandscapeOrientation()) {
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$2 */
    class C20272 implements OnLongClickListener {
        C20272() {
        }

        public boolean onLongClick(View v) {
            MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_SENSORS).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_SENSORS).getName());
            if (!MainActivity.isLandscapeOrientation()) {
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
            }
            return true;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$3 */
    class C20283 implements OnClickListener {
        C20283() {
        }

        public void onClick(View v) {
            MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_LIGHT).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_LIGHT).getName());
            if (!MainActivity.isLandscapeOrientation()) {
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$4 */
    class C20294 implements OnClickListener {
        C20294() {
        }

        public void onClick(View v) {
            MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_BLINDS).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_BLINDS).getName());
            if (!MainActivity.isLandscapeOrientation()) {
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$5 */
    class C20305 implements OnClickListener {
        C20305() {
        }

        public void onClick(View v) {
            if (NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE).size() == 1) {
                NVModel.CURR_ELEMENT = (VideophoneIP) NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE).get(0);
                Intent intent = new Intent().setClass(MainActivity.getContext(), ElementControl_VidIPActivity.class);
                intent.addFlags(67108864);
                MainActivity.this.startActivityForResult(intent, 0);
            } else if (NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE).size() > 1) {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_VIDEOPHONES).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_VIDEOPHONES).getName());
                if (!MainActivity.isLandscapeOrientation()) {
                    MainActivity.mainGrid.setVisibility(8);
                    MainActivity.fragment.setVisibility(0);
                }
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$6 */
    class C20316 implements OnLongClickListener {
        C20316() {
        }

        public boolean onLongClick(View v) {
            if (NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE).size() == 1) {
                NVModel.CURR_ELEMENT = (VideophoneIP) NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE).get(0);
                Intent intent = new Intent().setClass(MainActivity.getContext(), EditorVideophoneActivity.class);
                intent.addFlags(67108864);
                MainActivity.this.startActivityForResult(intent, 0);
                return true;
            } else if (NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE).size() <= 1) {
                return false;
            } else {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_VIDEOPHONES).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_VIDEOPHONES).getName());
                if (MainActivity.isLandscapeOrientation()) {
                    return true;
                }
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
                return true;
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$7 */
    class C20327 implements OnClickListener {
        C20327() {
        }

        public void onClick(View v) {
            MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_CAMERAS).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_CAMERAS).getName());
            if (!MainActivity.isLandscapeOrientation()) {
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$8 */
    class C20338 implements OnClickListener {
        C20338() {
        }

        public void onClick(View v) {
            MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_TEMPERATURE).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_TEMPERATURE).getName());
            if (!MainActivity.isLandscapeOrientation()) {
                MainActivity.mainGrid.setVisibility(8);
                MainActivity.fragment.setVisibility(0);
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.MainActivity$9 */
    class C20349 implements OnLongClickListener {
        C20349() {
        }

        public boolean onLongClick(View v) {
            Intent intent = new Intent().setClass(MainActivity.getContext(), EditorCategoryTemperatureActivity.class);
            intent.addFlags(67108864);
            MainActivity.this.startActivityForResult(intent, 0);
            return true;
        }
    }

    public class connectionToNexoGeolocation extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
            String toSend = params[0];
            Log.d("GeoService", "toSend value: " + toSend);
            if (NexoTalk.isConnected()) {
                try {
                    Thread.sleep(5000);
                    NexoTalk.send("@00000000:system L " + toSend + "\u0000");
                } catch (CommunicationException e) {
                    e.printStackTrace();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            } else {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                String ip = sharedPref.getString("pref_rememberme_login_ip", null);
                String passwd = sharedPref.getString("pref_rememberme_login_passwd", null);
                Boolean encryption = Boolean.valueOf(false);
                if (!(ip == null || ip.equals("") || passwd == null || passwd.equals(""))) {
                    MainActivity.connectToNexo(ip, passwd, encryption.booleanValue());
                }
                try {
                    Thread.sleep(5000);
                    NexoTalk.send("@00000000:system L " + toSend + "\u0000");
                } catch (CommunicationException e3) {
                    e3.printStackTrace();
                } catch (InterruptedException e22) {
                    e22.printStackTrace();
                }
            }
            return null;
        }
    }

    public class sendInfos extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            List<IElement> vid_list = NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE);
            if (vid_list.size() > 0 && vid_list.get(0) != null && (vid_list.get(0) instanceof VideophoneIP)) {
                MainActivity.sharedPrefs.edit().putString("pref_VidIP", ((VideophoneIP) vid_list.get(0)).getAddress()).commit();
            }
            boolean is_Connected = VidSockets.connect(MainActivity.sharedPrefs.getString("pref_VidIP", ""), "1026");
            Log.d("MainActivity", "(SendInfo) Is connected? " + is_Connected);
            if (is_Connected) {
                String mobileDevice = MainActivity.sharedPrefs.getString("pref_devicenumber", "");
                try {
                    VidSockets.send("GET /?setToken&mobilephone=" + mobileDevice + "&token=" + MainActivity.sharedPrefs.getString("pref_token", ""));
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
                VidSockets.disconnect();
            }
            return null;
        }
    }

    static {
        LanguageCodeList.add("xx");
        LanguageCodeList.add("de");
        LanguageCodeList.add("en");
        LanguageCodeList.add("pl");
        LanguageCodeList.add("ru");
        LanguageCodeList.add("sk");
        LanguageList.add("Auto");
        LanguageList.add("Deutsch");
        LanguageList.add("English");
        LanguageList.add("Polski");
        LanguageList.add("Pусский");
        LanguageList.add("Slovenčina");
        setDefaultRGBWColors();
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPrefs;
    }

    public static void setDefaultRGBWColors() {
        RGBWColors[0] = SupportMenu.CATEGORY_MASK;
        RGBWColors[1] = Color.rgb(255, 127, 0);
        RGBWColors[2] = InputDeviceCompat.SOURCE_ANY;
        RGBWColors[3] = -16711936;
        RGBWColors[4] = Color.rgb(0, 255, 255);
        RGBWColors[5] = -16776961;
        RGBWColors[6] = Color.rgb(255, 0, 255);
        RGBWColors[7] = -1;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        context = this;
        handler = new Handler();
        GeoService.isMainActivityRunning = true;
        NexoTalk.addNexoTalkListener(this);
        GeoService.addGeoListener(this);
        getWindow().addFlags(6815872);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.editor = sharedPrefs.edit();
        LanguageList.set(0, context.getString(R.string.PreferencesActivity_GlobalCat_Language_Auto));
        String languageToLoad = sharedPrefs.getString("pref_language", (String) LanguageCodeList.get(0));
        Locale locale;
        Configuration configuration;
        if (languageToLoad.equals("xx")) {
            String language = "xx";
            try {
                Process exec = Runtime.getRuntime().exec(new String[]{"getprop", "persist.sys.language"});
                language = new BufferedReader(new InputStreamReader(exec.getInputStream())).readLine();
                exec.destroy();
            } catch (IOException e) {
                language = Locale.getDefault().getLanguage();
            }
            Iterator it = LanguageCodeList.iterator();
            while (it.hasNext()) {
                CharSequence lang = (CharSequence) it.next();
                if (language.equals(lang.toString())) {
                    locale = new Locale(lang.toString());
                    Locale.setDefault(locale);
                    configuration = new Configuration();
                    configuration.locale = locale;
                    getBaseContext().getResources().updateConfiguration(configuration, null);
                }
            }
        } else {
            locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            configuration = new Configuration();
            configuration.locale = locale;
            getBaseContext().getResources().updateConfiguration(configuration, null);
        }
        setContentView(R.layout.activity_main);
        mainGrid = (GridLayout) findViewById(R.id.main_grid);
        fragment = (LinearLayout) findViewById(R.id.fragment);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (isLandscapeOrientation()) {
            mainGrid.setVisibility(0);
            fragment.setVisibility(0);
        }
        mainGrid.findViewById(R.id.cell_alarm).setOnClickListener(new C20251());
        mainGrid.findViewById(R.id.cell_alarm).setOnLongClickListener(new C20272());
        mainGrid.findViewById(R.id.cell_lights).setOnClickListener(new C20283());
        mainGrid.findViewById(R.id.cell_blinds).setOnClickListener(new C20294());
        mainGrid.findViewById(R.id.cell_videophone).setOnClickListener(new C20305());
        mainGrid.findViewById(R.id.cell_videophone).setOnLongClickListener(new C20316());
        mainGrid.findViewById(R.id.cell_videocameras).setOnClickListener(new C20327());
        mainGrid.findViewById(R.id.cell_temperature).setOnClickListener(new C20338());
        mainGrid.findViewById(R.id.cell_temperature).setOnLongClickListener(new C20349());
        mainGrid.findViewById(R.id.cell_places).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_PLACES).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_PLACES).getName());
                if (!MainActivity.isLandscapeOrientation()) {
                    MainActivity.mainGrid.setVisibility(8);
                    MainActivity.fragment.setVisibility(0);
                }
            }
        });
        mainGrid.findViewById(R.id.cell_places).setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_GEOLOCATION).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_GEOLOCATION).getName());
                if (!MainActivity.isLandscapeOrientation()) {
                    MainActivity.mainGrid.setVisibility(8);
                    MainActivity.fragment.setVisibility(0);
                }
                return true;
            }
        });
        mainGrid.findViewById(R.id.cell_others).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_AUTOMATION).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_AUTOMATION).getName());
                if (!MainActivity.isLandscapeOrientation()) {
                    MainActivity.mainGrid.setVisibility(8);
                    MainActivity.fragment.setVisibility(0);
                }
            }
        });
        mainGrid.findViewById(R.id.cell_gates).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_GATES).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_GATES).getName());
                if (!MainActivity.isLandscapeOrientation()) {
                    MainActivity.mainGrid.setVisibility(8);
                    MainActivity.fragment.setVisibility(0);
                }
            }
        });
        mainGrid.findViewById(R.id.cell_logics).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_LOGICS).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_LOGICS).getName());
                if (!MainActivity.isLandscapeOrientation()) {
                    MainActivity.mainGrid.setVisibility(8);
                    MainActivity.fragment.setVisibility(0);
                }
            }
        });
        Builder builder = new Builder(this);
        builder.setMessage(R.string.MainActivity_AppExitDialog_Question).setCancelable(false).setPositiveButton(R.string.MainActivity_AppExitDialog_SaveAndExit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!(XMLProject.defaultProject == null || XMLProject.defaultProject.isEmpty())) {
                    XMLProject.write(XMLProject.defaultProject);
                }
                ((Activity) MainActivity.context).finish();
            }
        }).setNeutralButton(R.string.MainActivity_AppExitDialog_Exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((Activity) MainActivity.context).finish();
            }
        }).setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        exitDialog = builder.create();
        exitDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (event.getAction() == 1 && keyCode == 4) {
                    MainActivity.exitDialog.dismiss();
                }
                return true;
            }
        });
        builder = new Builder(context);
        builder.setTitle(context.getString(R.string.PreferencesActivity_GlobalCat_DeviceNumber_DialogTitle));
        builder.setMessage(context.getString(R.string.PreferencesActivity_GlobalCat_DeviceNumber_DialogMessage));
        final EditText devNumInput = new EditText(context);
        devNumInput.setInputType(3);
        devNumInput.setGravity(17);
        builder.setView(devNumInput);
        builder.setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MainActivity.sharedPrefs.edit().putString("pref_devicenumber", devNumInput.getText().toString()).commit();
                Log.d("NexoVision", "Restarting VoipRRService");
                ((InputMethodManager) MainActivity.context.getSystemService("input_method")).hideSoftInputFromWindow(devNumInput.getWindowToken(), 0);
            }
        });
        builder.setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((InputMethodManager) MainActivity.context.getSystemService("input_method")).hideSoftInputFromWindow(devNumInput.getWindowToken(), 0);
            }
        });
        devNumberInputDialog = builder.create();
        builder = new Builder(context);
        builder.setTitle(context.getString(R.string.EditorActivity_SceneName_DialogTitle));
        final EditText sceneNameInput = new EditText(context);
        sceneNameInput.setGravity(17);
        builder.setView(sceneNameInput);
        builder.setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {

            /* renamed from: eu.nexwell.android.nexovision.MainActivity$21$1 */
            class C20261 implements Runnable {
                C20261() {
                }

                public void run() {
                    MainActivity.refreshFragment();
                }
            }

            public void onClick(DialogInterface dialog, int whichButton) {
                String sceneName = sceneNameInput.getText().toString();
                if (sceneName != null && !sceneName.isEmpty() && MainActivity.CURR_SET_ID > 0 && (NVModel.getElementById(Integer.valueOf(MainActivity.CURR_SET_ID)) instanceof ISet) && NVModel.getElementById(Integer.valueOf(MainActivity.CURR_SET_ID)).getType().equals(NVModel.EL_TYPE_SET)) {
                    Scene s = (Scene) NVModel.newElement(NVModel.EL_TYPE_SCENE);
                    s.setName(sceneName);
                    NVModel.addElement(s);
                    ((ISet) NVModel.getElementById(Integer.valueOf(MainActivity.CURR_SET_ID))).addElement(s);
                    s.createFromSet((ISet) NVModel.getElementById(Integer.valueOf(MainActivity.CURR_SET_ID)));
                }
                MainActivity.handler.post(new C20261());
            }
        });
        builder.setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        sceneNameInputDialog = builder.create();
        XMLProject.initModel(context);
        ArrayList<IElement> vidips = NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE);
        if (vidips.size() > 0) {
            this.editor.putString("pref_VidIP", ((VideophoneIP) vidips.get(0)).getAddress());
            this.editor.putString("pref_VIdSIP", ((VideophoneIP) vidips.get(0)).getSipProxy());
            this.editor.commit();
        } else {
            this.editor.putString("pref_VidIP", "");
            this.editor.putString("pref_VIdSIP", "");
            this.editor.commit();
        }
        this.vto = mainGrid.getViewTreeObserver();
        this.vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int outermargin = (int) MainActivity.this.getResources().getDimension(R.dimen.maingrid_outer_spacing);
                MainActivity.this.setIconSize(MainActivity.mainGrid, (MainActivity.mainGrid.getMeasuredWidth() - (((((int) MainActivity.this.getResources().getDimension(R.dimen.icon_flat_img_margin)) * 6) + (outermargin * 2)) + (((int) MainActivity.this.getResources().getDimension(R.dimen.maingrid_inner_spacing)) * 4))) / 3);
            }
        });
        connectionStatus = NexoTalk.isConnected();
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mReceiverComponent = new ComponentName(this, CallButtonIntentReceiver.class);
        this.mAudioManager.registerMediaButtonEventReceiver(this.mReceiverComponent);
        Permissions();
        boolean showGPSDialog = getIntent().getBooleanExtra("showGPSDialog", false);
        int GeoPointID = getIntent().getIntExtra("geolocationpointid", 0);
        if (showGPSDialog) {
            GeolocationPoint glp = (GeolocationPoint) NVModel.getElementById(Integer.valueOf(GeoPointID));
            if (GeoPointID != 0) {
                showGeolocationTrackerDialog(glp);
            }
        }
    }

    private void unlockScreen() {
        Window window = getWindow();
        window.addFlags(4194304);
        window.addFlags(524288);
        window.addFlags(2097152);
    }

    public static void setFragment(Fragment f, String name) {
        Log.e("MainActivity", "setFragment-Name: " + name);
        if (!((MainActivity) getContext()).isFinishing()) {
            FragmentTransaction transaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, f);
            if (name != null) {
                transaction.addToBackStack(name);
                Log.e("MainActivity", "ADD TO BACKSTACK(" + ((MainActivity) getContext()).getSupportFragmentManager().getBackStackEntryCount() + ")");
            }
            if ((f instanceof GridFragment) || (f instanceof RoomFragment)) {
                int i;
                if (isLandscapeOrientation() && (f instanceof GridFragment)) {
                    fragment.setPadding(0, dpToPixels(150), 0, 0);
                } else {
                    fragment.setPadding(0, 0, 0, 0);
                }
                if (NVModel.getElementById(Integer.valueOf(f.getArguments().getInt("id"))) instanceof Category) {
                }
                if (f.getArguments() != null) {
                    i = f.getArguments().getInt("id");
                } else {
                    i = 0;
                }
                CURR_SET_ID = i;
                Log.e("MainActivity", "setFragment-ID: " + CURR_SET_ID);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public static void refreshFragment() {
        Fragment f = ((MainActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (f != null && !((MainActivity) getContext()).isFinishing()) {
            FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
            ft.detach(f);
            ft.attach(f);
            ft.commitAllowingStateLoss();
        }
    }

    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "START");
        if (VERSION.SDK_INT <= 21) {
            this.alarm.setAlarm(this);
        } else {
            String token = FirebaseInstanceId.getInstance().getToken();
            sharedPrefs.edit().putString("pref_token", token).commit();
            Log.d("MainActivity", "Token: " + token);
        }
        NexoService.goBackground();
    }

    private void setIconSize(View view, int size) {
        int count;
        int i;
        View v;
        LayoutParams lp;
        if (view instanceof LinearLayout) {
            count = ((LinearLayout) view).getChildCount();
            for (i = 0; i <= count; i++) {
                v = ((LinearLayout) view).getChildAt(i);
                if (v instanceof SquareImageView) {
                    lp = ((SquareImageView) v).getLayoutParams();
                    lp.height = size;
                    lp.width = size;
                    ((SquareImageView) v).setLayoutParams(lp);
                    ((SquareImageView) v).requestLayout();
                } else if ((v instanceof LinearLayout) || (v instanceof GridLayout)) {
                    setIconSize(v, size);
                }
            }
        } else if (view instanceof GridLayout) {
            count = ((GridLayout) view).getChildCount();
            for (i = 0; i <= count; i++) {
                v = ((GridLayout) view).getChildAt(i);
                if (v instanceof SquareImageView) {
                    lp = ((SquareImageView) v).getLayoutParams();
                    lp.height = size;
                    lp.width = size;
                    ((SquareImageView) v).setLayoutParams(lp);
                    ((SquareImageView) v).requestLayout();
                } else if ((v instanceof LinearLayout) || (v instanceof GridLayout)) {
                    setIconSize(v, size);
                }
            }
        }
    }

    private static void refreshConnectionStatusIcon() {
        if (menu != null) {
            menu.getItem(0).setIcon(connectionStatus ? R.drawable.ic_online : R.drawable.ic_offline);
        }
    }

    private static void refreshConnectionStatus() {
        if (handler != null && NexoTalk.isConnected() != connectionStatus) {
            handler.post(new Runnable() {
                public void run() {
                    MainActivity.connectionStatus = NexoTalk.isConnected();
                    MainActivity.refreshConnectionStatusIcon();
                    Snackbar.make(MainActivity.fragment, MainActivity.connectionStatus ? R.string.NexoLoginActivity_Connected : R.string.NexoLoginActivity_Disconnected, 0).show();
                }
            });
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        refreshConnectionStatusIcon();
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onResume() {
        super.onResume();
        new sendInfos().execute(new Void[0]);
    }

    protected void onPostResume() {
        super.onPostResume();
        Log.d("MainActivity", "RESUME(" + CURR_SET_ID + ")");
        supportInvalidateOptionsMenu();
        VISIBLE = true;
        refreshFragment();
        NVModel.CURR_ELEMENT = null;
        NexoService.goForeground(NVModel.getCurrentElements());
        if (!(NexoService.getAlarming() == null || NexoService.getAlarming().isEmpty() || CURR_SET_ID == NVModel.getCategory(NVModel.CATEGORY_ALARM).getId().intValue())) {
            handler.post(new Runnable() {
                public void run() {
                    MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_ALARM).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_ALARM).getName());
                    if (!MainActivity.isLandscapeOrientation()) {
                        MainActivity.mainGrid.setVisibility(8);
                        MainActivity.fragment.setVisibility(0);
                    }
                }
            });
        }
        String device_number = sharedPrefs.getString("pref_devicenumber", "");
        if (device_number == null || device_number.equals("")) {
            devNumberInputDialog.getWindow().setSoftInputMode(4);
            devNumberInputDialog.show();
            return;
        }
        sharedPrefs.edit().putString("pref_devicenumber", device_number).commit();
    }

    public static Context getContext() {
        if (context == null) {
            return XMLProject.getLastContext();
        }
        return context;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!isLandscapeOrientation()) {
            menu.findItem(R.id.action_makepolygon).setVisible(false);
            menu.findItem(R.id.action_weather).setVisible(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment f;
        GridFragment gf;
        Intent intent;
        if (id == R.id.action_connect) {
            f = ((MainActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (f != null && (f instanceof GridFragment) && fragment.getVisibility() == 0) {
                gf = (GridFragment) f;
                if (gf.isEditMode()) {
                    gf.setEditMode(false);
                }
            }
            if (NexoTalk.isConnected()) {
                disconnectFromNexo(getContext());
            } else {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                String ip = sharedPref.getString("pref_rememberme_login_ip", null);
                String passwd = sharedPref.getString("pref_rememberme_login_passwd", null);
                Boolean encryption = Boolean.valueOf(false);
                if (ip == null || ip.equals("") || passwd == null || passwd.equals("")) {
                    intent = new Intent().setClass(getContext(), NexoLoginActivity.class);
                    intent.addFlags(67108864);
                    startActivityForResult(intent, 0);
                } else {
                    connectToNexo(ip, passwd, encryption.booleanValue());
                }
            }
        } else if (id == R.id.action_makescene) {
            IElement el = NVModel.getElementById(Integer.valueOf(CURR_SET_ID));
            if (CURR_SET_ID > 0 && (el instanceof ISet) && el.getType().equals(NVModel.EL_TYPE_SET)) {
                sceneNameInputDialog.show();
            }
            return true;
        } else if (id == R.id.action_makepolygon) {
            if (CURR_SET_ID > 0 && NVModel.getElementById(Integer.valueOf(CURR_SET_ID)) != null && (NVModel.getElementById(Integer.valueOf(CURR_SET_ID)) instanceof ISet) && NVModel.getElementById(Integer.valueOf(CURR_SET_ID)).getType().equals(NVModel.EL_TYPE_SET)) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.getContext(), "MAKE POLYGON", 1).show();
                    }
                });
                Polygon polygon = new Polygon();
                polygon.enableEditMode();
                RoomFragment.polygonInEditMode = polygon;
                NVModel.addElement(polygon);
                ((ISet) NVModel.getElementById(Integer.valueOf(CURR_SET_ID))).addElement(polygon);
                handler.post(new Runnable() {
                    public void run() {
                        MainActivity.refreshFragment();
                    }
                });
            }
            return true;
        } else if (id == R.id.action_project) {
            intent = new Intent().setClass(context, ProjectPreferencesActivity.class);
            intent.addFlags(67108864);
            startActivityForResult(intent, 0);
            return true;
        } else if (id == R.id.action_login) {
            intent = new Intent().setClass(getContext(), NexoLoginActivity.class);
            intent.addFlags(67108864);
            startActivityForResult(intent, 0);
            return true;
        } else if (id == R.id.action_preferences) {
            intent = new Intent().setClass(context, PreferencesActivity.class);
            intent.addFlags(67108864);
            startActivityForResult(intent, 0);
            return true;
        } else if (id == R.id.action_reorder) {
            if (!NexoTalk.isConnected()) {
                f = ((MainActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f != null && (f instanceof GridFragment) && fragment.getVisibility() == 0) {
                    gf = (GridFragment) f;
                    if (!gf.isEditMode()) {
                        gf.setEditMode(true);
                    }
                }
            }
            return true;
        } else if (id == R.id.action_weather) {
            intent = new Intent().setClass(context, WeatherActivity.class);
            intent.addFlags(67108864);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (RoomFragment.polygonInEditMode != null) {
                RoomFragment.polygonInEditMode.disableEditMode();
                RoomFragment.polygonInEditMode = null;
                refreshFragment();
                return true;
            } else if (RoomFragment.moveMode) {
                RoomFragment.moveMode = false;
                refreshFragment();
                return true;
            } else {
                Fragment f = ((MainActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (f != null && (f instanceof GridFragment) && fragment.getVisibility() == 0) {
                    GridFragment gf = (GridFragment) f;
                    if (gf.isEditMode()) {
                        gf.setEditMode(false);
                        return true;
                    }
                }
                ((MainActivity) getContext()).getSupportFragmentManager().popBackStack();
                Log.e("MainActivity", "POP FROM BACKSTACK(" + ((MainActivity) getContext()).getSupportFragmentManager().getBackStackEntryCount() + ")");
                if (((MainActivity) getContext()).getSupportFragmentManager().getBackStackEntryCount() < 2) {
                    Log.e("MainActivity", "BACKSTACK COUNT == 0");
                    if (mainGrid.getVisibility() == 0) {
                        exitDialog.show();
                        return true;
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            MainActivity.this.getSupportActionBar().setTitle(MainActivity.getContext().getString(R.string.APP_NAME));
                        }
                    });
                    CURR_SET_ID = 0;
                    NexoService.goForeground(NVModel.getCurrentElements());
                    if (!isLandscapeOrientation()) {
                        fragment.setVisibility(8);
                        mainGrid.setVisibility(0);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public void connectionProcessInfo(final String info, final String error) {
        if (handler != null) {
            if (!(error == null || error.isEmpty())) {
                Log.d("MainActivity", "connectionProcessInfo().ERROR: " + error);
            }
            handler.post(new Runnable() {
                public void run() {
                    if (info != null && !info.isEmpty()) {
                        if (MainActivity.connectionDialog == null) {
                            if (MainActivity.connectionDialogContext == null) {
                                MainActivity.connectionDialogContext = MainActivity.getContext();
                            }
                            MainActivity.connectionDialog = new ProgressDialog(MainActivity.connectionDialogContext);
                            MainActivity.connectionDialog.setCancelable(false);
                        }
                        if (MainActivity.connectionDialog != null) {
                            MainActivity.connectionDialog.setMessage(info);
                            try {
                                MainActivity.connectionDialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (MainActivity.connectionDialog != null) {
                        MainActivity.connectionDialog.dismiss();
                    }
                    if (error != null) {
                        if (!(error.isEmpty() || error.compareTo(MainActivity.lastDisplayedError) == 0)) {
                            MainActivity.displayInfo(error);
                        }
                        MainActivity.lastDisplayedError = error;
                    }
                }
            });
        }
    }

    public static void disconnectFromNexo(final Context context) {
        handler.post(new Runnable() {
            public void run() {
                if (MainActivity.connectionDialog != null) {
                    MainActivity.connectionDialog.dismiss();
                }
                MainActivity.connectionDialog = new ProgressDialog(context);
                MainActivity.connectionDialog.setCancelable(false);
                MainActivity.connectionDialog.setMessage(context.getString(R.string.ConnectionActivity_DisconnectionProgressDialog_Message));
                MainActivity.connectionDialog.show();
            }
        });
        NexoService.stop((MainActivity) getContext());
        handler.post(new Runnable() {
            public void run() {
                NVModel.setAllSwsUpdated(false);
                if (context == MainActivity.getContext()) {
                    MainActivity.refreshFragment();
                }
            }
        });
        if (connectionDialog != null) {
            handler.post(new Runnable() {
                public void run() {
                    MainActivity.connectionDialog.dismiss();
                }
            });
        }
    }

    public static void connectToNexo(String ip, String passwd, boolean encryption) {
        if (!NexoTalk.isConnected()) {
            String port = "1024";
            String[] target = ip.split(":");
            if (!(target.length <= 0 || target[0] == null || target[0].equals(""))) {
                if (target.length <= 1 || target[1] == null || target[1].equals("")) {
                    port = "1024";
                } else {
                    port = target[1];
                }
            }
            NexoService.start((MainActivity) getContext());
        }
    }

    private static void displayInfo(int messageId) {
        displayInfo(context, messageId);
    }

    private static void displayInfo(String message) {
        displayInfo(context, message);
    }

    public static void displayInfo(Context context, int messageId) {
        displayInfo(context, context.getString(messageId));
    }

    public static void displayInfo(Context context, String message) {
        Log.d("MainActivity", "Toast: " + message);
        Toast.makeText(context, context.getString(R.string.APP_NAME) + ": " + message, 1).show();
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy method");
        GeoService.isMainActivityRunning = false;
        this.mAudioManager.unregisterMediaButtonEventReceiver(this.mReceiverComponent);
        NexoTalk.removeNexoTalkListener(this);
        GeoService.removeGeoListener(this);
    }

    public void onImport(int type, int iterator) {
    }

    public void onImportEnd(ArrayList<Integer> arrayList) {
    }

    public void onStatusUpdate(IElement el, boolean finish) {
        handler.post(new Runnable() {
            public void run() {
                int totalLightsOn = 0;
                Iterator<IElement> itrl = NVModel.getCategory(NVModel.CATEGORY_LIGHT).getElements().iterator();
                while (itrl.hasNext()) {
                    IElement el = (IElement) itrl.next();
                    if (((el instanceof Light) || (el instanceof LightGroup) || (el instanceof Dimmer) || (el instanceof RGBW)) && ((Switch) el).isOn()) {
                        totalLightsOn++;
                    }
                }
                ((TextView) MainActivity.mainGrid.findViewById(R.id.cell_lights_value)).setText(totalLightsOn + "/" + NVModel.getCategory(NVModel.CATEGORY_LIGHT).getElements().size());
                TextView thermometer_out_value = (TextView) MainActivity.mainGrid.findViewById(R.id.cell_thermometers_value1);
                if (NVModel.getMainOutThermometer() != null) {
                    el = NVModel.getElementById(NVModel.getMainOutThermometer().getId());
                    if (el != null && (el instanceof Thermometer)) {
                        thermometer_out_value.setText(((Thermometer) el).getValue() + "℃");
                        thermometer_out_value.setVisibility(0);
                    }
                } else {
                    thermometer_out_value.setText("");
                    thermometer_out_value.setVisibility(8);
                }
                TextView thermometer_in_value = (TextView) MainActivity.mainGrid.findViewById(R.id.cell_thermometers_value2);
                if (NVModel.getMainInThermometer() != null) {
                    el = NVModel.getElementById(NVModel.getMainInThermometer().getId());
                    if (el != null && (el instanceof Thermometer)) {
                        thermometer_in_value.setText(((Thermometer) el).getValue() + "℃");
                        thermometer_in_value.setVisibility(0);
                        return;
                    }
                    return;
                }
                thermometer_in_value.setText("");
                thermometer_in_value.setVisibility(8);
            }
        });
    }

    public void onPartitionAlarm(IElement el) {
        Fragment f = ((MainActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (f != null && (f instanceof GridFragment)) {
            CURR_SET_ID = ((GridFragment) f).getArguments() != null ? ((GridFragment) f).getArguments().getInt("id") : 0;
        }
        if (el != null && CURR_SET_ID != NVModel.getCategory(NVModel.CATEGORY_ALARM).getId().intValue()) {
            final IElement e = el;
            Log.e("MainActivity", "!!! ALARM(" + e.getName() + ") !!!");
            handler.post(new Runnable() {
                public void run() {
                    if (MainActivity.VISIBLE) {
                        Log.e("MainActivity", "!!! SET ALARM FRAGMENT(" + e.getName() + ") !!!");
                        MainActivity.setFragment(GridFragment.newInstance(NVModel.getCategory(NVModel.CATEGORY_ALARM).getId().intValue()), NVModel.getCategory(NVModel.CATEGORY_ALARM).getName());
                        if (!MainActivity.isLandscapeOrientation()) {
                            MainActivity.mainGrid.setVisibility(8);
                            MainActivity.fragment.setVisibility(0);
                        }
                    }
                }
            });
        }
    }

    public void connectionStatus(boolean connected) {
        refreshConnectionStatus();
        if (connected && VISIBLE) {
            NexoService.goForeground(NVModel.getCurrentElements());
        }
    }

    private static int dpToPixels(int dps) {
        if (dps == 0) {
            return 0;
        }
        return (int) ((((float) dps) * ((MainActivity) getContext()).getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void onLocationUpdate(Location loc) {
    }

    public void onStateChange(final GeolocationPoint glp, float distance) {
        handler.post(new Runnable() {
            public void run() {
                MainActivity.this.unlockScreen();
                MainActivity.this.showGeolocationTrackerDialog(glp);
            }
        });
    }

    public void showGeolocationTrackerDialog(GeolocationPoint glp) {
        Builder builder = new Builder(context);
        builder.setTitle(context.getString(R.string.GeolocationActivity_GeolocationTrackerDialog_Title));
        builder.setCancelable(false);
        builder.setNegativeButton(context.getString(R.string.NO), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        final String dataToSend;
        if (glp.getState().equals(GeolocationPoint.GEOLOCP_STATE_IN)) {
            dataToSend = glp.getOnEnterLogic().getAction1();
            builder.setMessage(glp.getOnEnterMessage());
            builder.setPositiveButton(context.getString(R.string.YES), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new connectionToNexoGeolocation().execute(new String[]{dataToSend});
                }
            });
        } else if (glp.getState().equals(GeolocationPoint.GEOLOCP_STATE_OUT)) {
            dataToSend = glp.getOnExitLogic().getAction1();
            builder.setMessage(glp.getOnExitMessage());
            builder.setPositiveButton(context.getString(R.string.YES), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new connectionToNexoGeolocation().execute(new String[]{dataToSend});
                }
            });
        }
        builder.create().show();
    }

    public void onStateUpdate(GeolocationPoint glp, float distance) {
    }

    public static boolean isLandscapeOrientation() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float smallestWidth = Math.min(((float) widthPixels) / scaleFactor, ((float) heightPixels) / scaleFactor);
        return false;
    }

    private void Permissions() {
        if (VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList();
            final List<String> permissionsList = new ArrayList();
            if (!addPermission(permissionsList, "android.permission.ACCESS_FINE_LOCATION")) {
                permissionsNeeded.add("GPS");
            }
            if (!addPermission(permissionsList, "android.permission.READ_EXTERNAL_STORAGE")) {
                permissionsNeeded.add("Read Storage");
            }
            if (!addPermission(permissionsList, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                permissionsNeeded.add("Write Storage");
            }
            if (!addPermission(permissionsList, "android.permission.RECORD_AUDIO")) {
                permissionsNeeded.add("Handle Audio");
            }
            if (!addPermission(permissionsList, "android.permission.CAMERA")) {
                permissionsNeeded.add("Handle Camera");
            }
            if (permissionsList.size() <= 0) {
                return;
            }
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + ((String) permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + ((String) permissionsNeeded.get(i));
                }
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = 23)
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.requestPermissions((String[]) permissionsList.toArray(new String[permissionsList.size()]), MainActivity.REQUEST_CODE_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            requestPermissions((String[]) permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_MULTIPLE_PERMISSIONS);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new Builder(this).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show();
    }

    @RequiresApi(api = 23)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != 0) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_MULTIPLE_PERMISSIONS /*666*/:
                Map<String, Integer> perms = new HashMap();
                perms.put("android.permission.ACCESS_FINE_LOCATION", Integer.valueOf(0));
                perms.put("android.permission.READ_EXTERNAL_STORAGE", Integer.valueOf(0));
                perms.put("android.permission.WRITE_EXTERNAL_STORAGE", Integer.valueOf(0));
                perms.put("android.permission.RECORD_AUDIO", Integer.valueOf(0));
                perms.put("android.permission.CAMERA", Integer.valueOf(0));
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], Integer.valueOf(grantResults[i]));
                }
                if (((Integer) perms.get("android.permission.ACCESS_FINE_LOCATION")).intValue() != 0 || ((Integer) perms.get("android.permission.READ_EXTERNAL_STORAGE")).intValue() != 0 || ((Integer) perms.get("android.permission.WRITE_EXTERNAL_STORAGE")).intValue() != 0 || ((Integer) perms.get("android.permission.RECORD_AUDIO")).intValue() != 0 || ((Integer) perms.get("android.permission.CAMERA")).intValue() != 0) {
                    Toast.makeText(this, "Some Permission is Denied", 0).show();
                    return;
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    public static boolean isTablet() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float yInches = ((float) displayMetrics.heightPixels) / displayMetrics.ydpi;
        float xInches = ((float) displayMetrics.widthPixels) / displayMetrics.xdpi;
        if (Math.sqrt((double) ((xInches * xInches) + (yInches * yInches))) >= 6.5d) {
            return true;
        }
        return false;
    }

    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause method");
    }

    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop method");
    }
}
