package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.camera.simplemjpeg.MjpegInputStream;
import com.camera.simplemjpeg.MjpegView;
import com.google.android.gms.search.SearchAuth.StatusCodes;
import eu.nexwell.android.nexovision.communication.CommunicationException;
import eu.nexwell.android.nexovision.misc.XMLProject;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import nexovision.android.nexwell.eu.nexovision.R;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.linphone.mini.LinphoneMiniManager;
import org.linphone.mini.VidSockets;

public class ElementControl_VidIPActivity extends AppCompatActivity implements SensorEventListener {
    private static final String LOG_TAG = "VidActivity";
    public static SquareImageButton buttonAcceptMakeCall;
    public static SquareImageButton buttonDeclineCall;
    public static boolean isVisible = false;
    private static boolean micOn = true;
    private static boolean speakerOn = false;
    private AudioManager am;
    private SquareImageButton buttonMicOnOff;
    private SquareImageButton buttonOpenGate;
    private SquareImageButton buttonSpeakerOnOff;
    private TextView callInfo;
    private Context context;
    private EditText edittextCallAddress;
    private Handler handler;
    private int height;
    private boolean isProximityAffected;
    private boolean mFocusDuringOnPause;
    private LinphoneMiniManager mLManager;
    private PowerManager mPowerManager;
    private Sensor mProximity;
    private SensorManager mSensorManager;
    private WakeLock mWakeLock;
    private LinearLayout mjpegFreezed;
    private TextView mjpegInfo;
    private MjpegView mjpegView;
    private SharedPreferences sharedPrefs;
    private boolean showButtonDecline;
    private String url;
    private int width;

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$1 */
    class C19711 implements OnClickListener {
        C19711() {
        }

        public void onClick(View v) {
            ElementControl_VidIPActivity.this.startCall();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$2 */
    class C19722 implements OnClickListener {
        C19722() {
        }

        public void onClick(View v) {
            ElementControl_VidIPActivity.this.stopCall();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$3 */
    class C19733 implements OnClickListener {
        C19733() {
        }

        public void onClick(View v) {
            ElementControl_VidIPActivity.this.changeStateSpeaker();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$4 */
    class C19744 implements OnClickListener {
        C19744() {
        }

        public void onClick(View v) {
            ElementControl_VidIPActivity.this.changeStateMic();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$5 */
    class C19755 implements OnClickListener {
        C19755() {
        }

        public void onClick(View v) {
            ElementControl_VidIPActivity.this.gateOpen();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$6 */
    class C19766 implements OnClickListener {
        C19766() {
        }

        public void onClick(View v) {
            ElementControl_VidIPActivity.this.changeFreezeState();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$7 */
    class C19777 implements Runnable {
        C19777() {
        }

        public void run() {
            ElementControl_VidIPActivity.this.buttonOpenGate.setImageResource(R.drawable.ic_lock_opened);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$8 */
    class C19788 implements Runnable {
        C19788() {
        }

        public void run() {
            ElementControl_VidIPActivity.this.buttonOpenGate.setImageResource(R.drawable.ic_lock_closed);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$9 */
    class C19799 implements Runnable {
        C19799() {
        }

        public void run() {
            ElementControl_VidIPActivity.this.callInfo.setText(NVModel.CURR_ELEMENT.getName() + " - " + ElementControl_VidIPActivity.this.getString(R.string.ELCVideophoneActivity_ConversationInProgress));
            ElementControl_VidIPActivity.buttonDeclineCall.setVisibility(0);
            ElementControl_VidIPActivity.buttonAcceptMakeCall.setVisibility(4);
        }
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$DoRead$1 */
        class C19801 implements Runnable {
            C19801() {
            }

            public void run() {
                ElementControl_VidIPActivity.this.mjpegInfo.setVisibility(8);
                ElementControl_VidIPActivity.this.mjpegView.setVisibility(0);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$DoRead$2 */
        class C19812 implements Runnable {
            C19812() {
            }

            public void run() {
                ElementControl_VidIPActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_VidIPActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_VidIPActivity.this.mjpegView.setVisibility(4);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$DoRead$3 */
        class C19823 implements Runnable {
            C19823() {
            }

            public void run() {
                ElementControl_VidIPActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_VidIPActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_VidIPActivity.this.mjpegView.setVisibility(4);
            }
        }

        protected MjpegInputStream doInBackground(String... _url) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            HttpConnectionParams.setSoTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            Log.e(ElementControl_VidIPActivity.LOG_TAG, "DO_READ (VISIBLE=" + ElementControl_VidIPActivity.isVisible + ")");
            while (ElementControl_VidIPActivity.isVisible) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse res = httpclient.execute(new HttpGet(URI.create(_url[0].replaceAll("\\s", "") + ":8888")));
                    if (res.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                        Log.e(ElementControl_VidIPActivity.LOG_TAG, "ERR 401");
                        return null;
                    }
                    Log.e(ElementControl_VidIPActivity.LOG_TAG, "res=" + res.getStatusLine().getStatusCode());
                    ElementControl_VidIPActivity.this.handler.post(new C19801());
                    return new MjpegInputStream(res.getEntity().getContent());
                } catch (ClientProtocolException e2) {
                    e2.printStackTrace();
                    Log.e(ElementControl_VidIPActivity.LOG_TAG, "Request failed-ClientProtocolException", e2);
                    ElementControl_VidIPActivity.this.handler.post(new C19812());
                } catch (IOException e3) {
                    e3.printStackTrace();
                    Log.e(ElementControl_VidIPActivity.LOG_TAG, "Request failed-IOException", e3);
                    ElementControl_VidIPActivity.this.handler.post(new C19823());
                }
            }
            return null;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (NVModel.CURR_ELEMENT instanceof VideophoneIP) {
                ElementControl_VidIPActivity.this.width = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().x;
                ElementControl_VidIPActivity.this.height = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().y;
                if (ElementControl_VidIPActivity.this.width >= 1 && ElementControl_VidIPActivity.this.height >= 1 && ElementControl_VidIPActivity.this.url != null && !ElementControl_VidIPActivity.this.url.equals("")) {
                    ElementControl_VidIPActivity.this.mjpegView.setResolution(ElementControl_VidIPActivity.this.width, ElementControl_VidIPActivity.this.height);
                }
            }
            ElementControl_VidIPActivity.this.mjpegView.setSource(result);
            if (result != null) {
                result.setSkip(1);
            }
            ElementControl_VidIPActivity.this.mjpegView.setDisplayMode(5);
            ElementControl_VidIPActivity.this.mjpegView.showFps(true);
        }
    }

    public class checkVidipBoxFWVersion extends AsyncTask<Void, Void, Void> {
        private int fwStringToInt(String version) {
            String[] ver = version.split("[.]");
            int v = 0;
            int i = 0;
            while (i < 4) {
                int newNumber;
                try {
                    newNumber = i < ver.length ? Integer.parseInt(ver[i]) : 0;
                } catch (NumberFormatException e) {
                    newNumber = 0;
                }
                v = (v * 100) + newNumber;
                i++;
            }
            return v;
        }

        protected Void doInBackground(Void... params) {
            VidSockets.connect(ElementControl_VidIPActivity.this.sharedPrefs.getString("pref_VidIP", ""), "1026");
            try {
                String[] response = VidSockets.sendAndRead("GET /?firmwareVersion").split("\\r?\\n");
                final String vidipBoxFWVersion = response.length > 0 ? response[response.length - 1] : "0";
                if (fwStringToInt(vidipBoxFWVersion) < fwStringToInt("7.6.13")) {
                    ElementControl_VidIPActivity.this.handler.post(new Runnable() {

                        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VidIPActivity$checkVidipBoxFWVersion$1$1 */
                        class C19831 implements DialogInterface.OnClickListener {
                            C19831() {
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ElementControl_VidIPActivity) ElementControl_VidIPActivity.this.context).finish();
                            }
                        }

                        public void run() {
                            new Builder(ElementControl_VidIPActivity.this.context).setTitle(ElementControl_VidIPActivity.this.context.getString(R.string.ELCVideophoneActivity_NeedFirmwareUpdate_Title)).setMessage(String.format(ElementControl_VidIPActivity.this.context.getString(R.string.ELCVideophoneActivity_NeedFirmwareUpdate_Message), new Object[]{vidipBoxFWVersion})).setPositiveButton(ElementControl_VidIPActivity.this.context.getString(R.string.OK), new C19831()).setIcon(17301543).setCancelable(false).show();
                        }
                    });
                }
                if (fwStringToInt(vidipBoxFWVersion) < fwStringToInt("8.1.24")) {
                    if (NVModel.CURR_ELEMENT instanceof VideophoneIP) {
                        ((VideophoneIP) NVModel.CURR_ELEMENT).setSize(640, 480);
                    }
                    ElementControl_VidIPActivity.this.mjpegView.setResolution(640, 480);
                } else {
                    if (NVModel.CURR_ELEMENT instanceof VideophoneIP) {
                        ((VideophoneIP) NVModel.CURR_ELEMENT).setSize(1024, 768);
                    }
                    ElementControl_VidIPActivity.this.mjpegView.setResolution(1024, 768);
                }
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VidSockets.disconnect();
            return null;
        }

        protected void onPostExecute(Void result) {
            new DoRead().execute(new String[]{ElementControl_VidIPActivity.this.url});
        }
    }

    public class endCall extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VidSockets.connect(ElementControl_VidIPActivity.this.sharedPrefs.getString("pref_VidIP", ""), "1026");
            try {
                VidSockets.send("GET /?streamStop");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VidSockets.disconnect();
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public class gateRequest extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VidSockets.connect(ElementControl_VidIPActivity.this.sharedPrefs.getString("pref_VidIP", ""), "1026");
            try {
                VidSockets.send("GET /?gateRequest");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VidSockets.disconnect();
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public class makeCall extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VidSockets.connect(ElementControl_VidIPActivity.this.sharedPrefs.getString("pref_VidIP", ""), "1026");
            try {
                VidSockets.send("GET /?streamStart");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VidSockets.disconnect();
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public class sendAcceptForNewIncomeCall extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VidSockets.connect(ElementControl_VidIPActivity.this.sharedPrefs.getString("pref_VidIP", ""), "1026");
            try {
                VidSockets.send("GET /?resetRedirect&mobilephone=" + ElementControl_VidIPActivity.this.sharedPrefs.getString("pref_devicenumber", ""));
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VidSockets.disconnect();
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_elementcontrol_videophone);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().addFlags(6815872);
        isVisible = true;
        this.handler = new Handler();
        this.context = this;
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.mSensorManager = (SensorManager) getSystemService("sensor");
        this.mProximity = this.mSensorManager.getDefaultSensor(8);
        this.mPowerManager = (PowerManager) getSystemService("power");
        this.mWakeLock = this.mPowerManager.newWakeLock(32, getLocalClassName());
        this.am = (AudioManager) getSystemService("audio");
        this.mjpegView = null;
        this.mjpegInfo = null;
        this.mjpegFreezed = null;
        this.callInfo = null;
        XMLProject.initModel(this.context);
        this.mLManager = new LinphoneMiniManager(this);
        this.edittextCallAddress = (EditText) findViewById(R.id.edittextCallAddress);
        buttonAcceptMakeCall = (SquareImageButton) findViewById(R.id.buttonAcceptMakeCall);
        buttonDeclineCall = (SquareImageButton) findViewById(R.id.buttonDeclineCall);
        this.buttonSpeakerOnOff = (SquareImageButton) findViewById(R.id.buttonSpeakerOnOff);
        this.buttonMicOnOff = (SquareImageButton) findViewById(R.id.buttonMicOnOff);
        this.buttonOpenGate = (SquareImageButton) findViewById(R.id.buttonOpenGate);
        this.callInfo = (TextView) findViewById(R.id.callInfo);
        this.mjpegInfo = (TextView) findViewById(R.id.mjpegInfo);
        this.mjpegFreezed = (LinearLayout) findViewById(R.id.mjpegFreezed);
        this.mjpegView = (MjpegView) findViewById(R.id.mv);
        this.mjpegFreezed.setVisibility(8);
        this.edittextCallAddress.setVisibility(4);
        this.edittextCallAddress.setText(this.sharedPrefs.getString("pref_VIdSIP", ""));
        buttonAcceptMakeCall.setOnClickListener(new C19711());
        this.showButtonDecline = getIntent().getBooleanExtra("buttonDecline", false);
        if (this.showButtonDecline) {
            buttonDeclineCall.setVisibility(0);
        } else {
            buttonDeclineCall.setVisibility(4);
        }
        buttonDeclineCall.setOnClickListener(new C19722());
        this.buttonSpeakerOnOff.setOnClickListener(new C19733());
        this.buttonMicOnOff.setOnClickListener(new C19744());
        this.buttonOpenGate.setImageResource(R.drawable.ic_lock_closed);
        this.buttonOpenGate.setOnClickListener(new C19755());
        if (NVModel.CURR_ELEMENT == null || !(NVModel.CURR_ELEMENT instanceof VideophoneIP)) {
            ArrayList<IElement> vidips = NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE);
            if (vidips.size() < 1) {
                Log.d(LOG_TAG, "Vidips.size < 1");
                return;
            }
            NVModel.CURR_ELEMENT = (IElement) vidips.get(0);
        }
        this.url = Uri.decode("http://" + ((VideophoneIP) NVModel.CURR_ELEMENT).getAddress());
        new checkVidipBoxFWVersion().execute(new Void[0]);
        this.mjpegView.setOnClickListener(new C19766());
    }

    protected void onResume() {
        boolean z = false;
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        this.mSensorManager.registerListener(this, this.mProximity, 3);
        if (!this.isProximityAffected) {
            AudioManager am = (AudioManager) getSystemService("audio");
            am.setMicrophoneMute(false);
            am.setMode(0);
            speakerOn = am.isSpeakerphoneOn();
            refreshSpeakerIcon();
            if (!am.isMicrophoneMute()) {
                z = true;
            }
            micOn = z;
            refreshMicIcon();
        }
    }

    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        this.mFocusDuringOnPause = hasWindowFocus();
        this.mSensorManager.unregisterListener(this);
    }

    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        if (this.mFocusDuringOnPause && !this.isProximityAffected) {
            stopCallonPause();
        }
    }

    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        isVisible = false;
        this.am.setMode(0);
        this.mLManager.destroy();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ec_videophone, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_snapshot) {
            takeSnapshot();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshSpeakerIcon() {
        this.buttonSpeakerOnOff.setImageResource(speakerOn ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
    }

    private void refreshMicIcon() {
        this.buttonMicOnOff.setImageResource(micOn ? R.drawable.ic_mic_on : R.drawable.ic_mic_off);
    }

    private void changeStateMic() {
        boolean z;
        boolean z2 = true;
        if (micOn) {
            z = false;
        } else {
            z = true;
        }
        micOn = z;
        AudioManager am = (AudioManager) getSystemService("audio");
        if (micOn) {
            z2 = false;
        }
        am.setMicrophoneMute(z2);
        refreshMicIcon();
    }

    private void changeStateSpeaker() {
        speakerOn = !speakerOn;
        ((AudioManager) getSystemService("audio")).setSpeakerphoneOn(speakerOn);
        refreshSpeakerIcon();
    }

    private void gateOpen() {
        new gateRequest().execute(new Void[0]);
        this.handler.post(new C19777());
        this.handler.postDelayed(new C19788(), 5000);
    }

    private void startCall() {
        VidSockets.callInProgress = true;
        VidSockets.incomingCall = false;
        if (VidSockets.f283r != null && VidSockets.f283r.isPlaying()) {
            VidSockets.f283r.stop();
        }
        if (this.mLManager != null) {
            new makeCall().execute(new Void[0]);
            Log.d(LOG_TAG, "mManager.newOutgoingCall() --> " + this.edittextCallAddress.getText().toString());
            this.mLManager.newOutgoingCall(this.edittextCallAddress.getText().toString(), this.edittextCallAddress.getText().toString());
            this.handler.post(new C19799());
        }
    }

    private void stopCall() {
        if (VidSockets.f283r != null && VidSockets.f283r.isPlaying()) {
            VidSockets.f283r.stop();
        }
        if (VidSockets.callInProgress) {
            if (this.mLManager != null) {
                this.mLManager.terminateCall();
                new endCall().execute(new Void[0]);
                this.handler.post(new Runnable() {
                    public void run() {
                        ElementControl_VidIPActivity.this.callInfo.setText(NVModel.CURR_ELEMENT.getName());
                        ElementControl_VidIPActivity.buttonAcceptMakeCall.setVisibility(0);
                        ElementControl_VidIPActivity.buttonDeclineCall.setVisibility(4);
                    }
                });
            }
            new sendAcceptForNewIncomeCall().execute(new Void[0]);
            VidSockets.callInProgress = false;
            return;
        }
        new sendAcceptForNewIncomeCall().execute(new Void[0]);
        VidSockets.incomingCall = false;
        finish();
    }

    private void stopCallonPause() {
        if (VidSockets.f283r != null && VidSockets.f283r.isPlaying()) {
            VidSockets.f283r.stop();
        }
        if (VidSockets.callInProgress) {
            VidSockets.incomingCall = false;
            new sendAcceptForNewIncomeCall().execute(new Void[0]);
            if (this.mLManager != null) {
                this.mLManager.terminateCall();
                new endCall().execute(new Void[0]);
                finish();
                return;
            }
            return;
        }
        VidSockets.incomingCall = false;
        new sendAcceptForNewIncomeCall().execute(new Void[0]);
        finish();
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != 8) {
            return;
        }
        if (event.values[0] == 0.0f) {
            sleepScreen(true);
            this.isProximityAffected = true;
            Log.d(LOG_TAG, "isProximityAffacted " + this.isProximityAffected);
            return;
        }
        sleepScreen(false);
        this.isProximityAffected = false;
        Log.d(LOG_TAG, "isProximityAffacted " + this.isProximityAffected);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void sleepScreen(boolean on) {
        if (on) {
            if (!this.mWakeLock.isHeld()) {
                this.mWakeLock.acquire();
            }
        } else if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    private void changeFreezeState() {
        if (this.mjpegView.isFreezedPlayback()) {
            this.mjpegView.unfreezePlayback();
            this.mjpegFreezed.setVisibility(8);
            return;
        }
        this.mjpegView.freezePlayback();
        LayoutParams lp = (LayoutParams) this.mjpegFreezed.getLayoutParams();
        lp.setMargins(0, 0, 0, this.mjpegView.getMeasuredHeight() / 6);
        this.mjpegFreezed.setLayoutParams(lp);
        this.mjpegFreezed.setVisibility(0);
    }

    public void takeSnapshot() {
        Exception e;
        Throwable th;
        if (this.mjpegView == null) {
            return;
        }
        if (this.mjpegView.isStreaming() || this.mjpegView.isFreezedPlayback()) {
            String path = MainActivity.getSharedPreferences().getString("pref_systemcamshotspath", Environment.getExternalStorageDirectory().toString());
            FileOutputStream out = null;
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Long.valueOf(new Date().getTime()));
            String filename = "VIDIP_" + date + "_" + NVModel.CURR_ELEMENT.getName().replace(' ', '_') + ".jpg";
            try {
                FileOutputStream out2 = new FileOutputStream(path + File.separator + filename);
                try {
                    if (!this.mjpegView.isFreezedPlayback()) {
                        this.mjpegView.freezePlayback();
                        this.mjpegView.waitFreezed();
                    }
                    this.mjpegView.getBitmap().compress(CompressFormat.JPEG, 80, out2);
                    if (out2 != null) {
                        try {
                            out2.close();
                            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(path + File.separator + filename).getAbsolutePath()}, null, null);
                            Snackbar.make(findViewById(R.id.container), "Snapshot saved to\n" + path + File.separator + filename, 0).show();
                            if (true) {
                                this.mjpegView.unfreezePlayback();
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    out = out2;
                    try {
                        e.printStackTrace();
                        if (out != null) {
                            try {
                                out.close();
                                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(path + File.separator + filename).getAbsolutePath()}, null, null);
                                Snackbar.make(findViewById(R.id.container), "Snapshot saved to\n" + path + File.separator + filename, 0).show();
                                if (null != null) {
                                    this.mjpegView.unfreezePlayback();
                                }
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (out != null) {
                            try {
                                out.close();
                                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(path + File.separator + filename).getAbsolutePath()}, null, null);
                                Snackbar.make(findViewById(R.id.container), "Snapshot saved to\n" + path + File.separator + filename, 0).show();
                                if (null != null) {
                                    this.mjpegView.unfreezePlayback();
                                }
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    out = out2;
                    if (out != null) {
                        out.close();
                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(path + File.separator + filename).getAbsolutePath()}, null, null);
                        Snackbar.make(findViewById(R.id.container), "Snapshot saved to\n" + path + File.separator + filename, 0).show();
                        if (null != null) {
                            this.mjpegView.unfreezePlayback();
                        }
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                e.printStackTrace();
                if (out != null) {
                    out.close();
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(path + File.separator + filename).getAbsolutePath()}, null, null);
                    Snackbar.make(findViewById(R.id.container), "Snapshot saved to\n" + path + File.separator + filename, 0).show();
                    if (null != null) {
                        this.mjpegView.unfreezePlayback();
                    }
                }
            }
        }
    }

    private void startVideoStream() {
        if (this.mjpegView != null) {
            Log.d(LOG_TAG, "startVideoStream");
            this.width = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().x;
            this.height = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().y;
            if (this.width >= 1 && this.height >= 1 && this.url != null && !this.url.equals("")) {
                Log.d(LOG_TAG, "url=" + this.url);
                this.mjpegView.setResolution(this.width, this.height);
                if (this.url != null && !this.url.isEmpty()) {
                    this.mjpegView.startPlayback();
                    Log.d(LOG_TAG, "Video stream has been started");
                }
            }
        }
    }

    private void stopVideoStream() {
        if (this.mjpegView != null) {
            Log.d(LOG_TAG, "stopVideoStream");
            if (this.mjpegView.isStreaming()) {
                this.mjpegView.stopPlayback();
            }
            this.mjpegView.freeCameraMemory();
        }
    }
}
