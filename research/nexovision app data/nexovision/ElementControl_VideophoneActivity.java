package eu.nexwell.android.nexovision;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.camera.simplemjpeg.MjpegInputStream;
import com.camera.simplemjpeg.MjpegView;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.search.SearchAuth.StatusCodes;
import eu.nexwell.android.nexovision.communication.CommunicationException;
import eu.nexwell.android.nexovision.misc.XMLProject;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.linphone.mediastream.video.capture.hwconf.Hacks;
import org.linphone.mini.LinphoneMiniManager;
import org.linphone.mini.VoipRRService;

public class ElementControl_VideophoneActivity extends AppCompatActivity implements SensorEventListener {
    private static String LOG_TAG = "EC_VideophoneActivity";
    private static final int REQUEST_SETTINGS = 0;
    public static boolean VISIBLE;
    public static SquareImageButton buttonAcceptMakeCall;
    public static SquareImageButton buttonDeclineCall;
    public static ProgressDialog connectDialog;
    private static Context context;
    private static Handler handler;
    private static boolean micOn = true;
    private static boolean onPauseSetting = false;
    private static boolean speakerOn = false;
    public SquareImageButton buttonMicOnOff;
    public SquareImageButton buttonOpenGate;
    public SquareImageButton buttonSpeakerOnOff;
    private TextView callInfo = null;
    public EditText edittextCallAddress;
    private int height;
    private CallButtonIntentReceiver mMediaButtonReceiver;
    private WakeLock mWakeLock;
    private LinearLayout mjpegFreezed = null;
    private TextView mjpegInfo = null;
    private MjpegView mjpegView = null;
    private Sensor myProximitySensor;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("AA", "ON RECEIVE");
        }
    };
    private SensorManager mySensorManager;
    private boolean proximitySensorAffected = false;
    private String url;
    private boolean videoStreamError = false;
    private ViewTreeObserver vto;
    private int width;

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$1 */
    class C19861 implements OnClickListener {
        C19861() {
        }

        public void onClick(View v) {
            if (ElementControl_VideophoneActivity.this.mjpegView.isFreezedPlayback()) {
                ElementControl_VideophoneActivity.this.mjpegView.unfreezePlayback();
                ElementControl_VideophoneActivity.this.mjpegFreezed.setVisibility(8);
                return;
            }
            ElementControl_VideophoneActivity.this.mjpegView.freezePlayback();
            LayoutParams lp = (LayoutParams) ElementControl_VideophoneActivity.this.mjpegFreezed.getLayoutParams();
            lp.setMargins(0, 0, 0, ElementControl_VideophoneActivity.this.mjpegView.getMeasuredHeight() / 6);
            ElementControl_VideophoneActivity.this.mjpegFreezed.setLayoutParams(lp);
            ElementControl_VideophoneActivity.this.mjpegFreezed.setVisibility(0);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$2 */
    class C19882 implements OnClickListener {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$2$1 */
        class C19871 implements Runnable {
            C19871() {
            }

            public void run() {
                ElementControl_VideophoneActivity.this.callInfo.setText(NVModel.CURR_ELEMENT.getName() + " - " + ElementControl_VideophoneActivity.this.getString(R.string.ELCVideophoneActivity_ConversationInProgress));
                ElementControl_VideophoneActivity.buttonDeclineCall.setVisibility(0);
                ElementControl_VideophoneActivity.buttonAcceptMakeCall.setVisibility(4);
            }
        }

        C19882() {
        }

        public void onClick(View v) {
            VoipRRService.callInProgress = true;
            VoipRRService.incomingCall = false;
            new makeCall().execute(new Void[0]);
            if (VoipRRService.f284r != null && VoipRRService.f284r.isPlaying()) {
                VoipRRService.f284r.stop();
            }
            if (LinphoneMiniManager.getInstance() != null) {
                Log.d(ElementControl_VideophoneActivity.LOG_TAG, "mManager.newOutgoingCall() --> " + ElementControl_VideophoneActivity.this.edittextCallAddress.getText().toString());
                LinphoneMiniManager.getInstance().newOutgoingCall(ElementControl_VideophoneActivity.this.edittextCallAddress.getText().toString(), ElementControl_VideophoneActivity.this.edittextCallAddress.getText().toString());
            }
            ElementControl_VideophoneActivity.handler.post(new C19871());
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$3 */
    class C19913 implements OnClickListener {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$3$1 */
        class C19891 implements Runnable {
            C19891() {
            }

            public void run() {
                ElementControl_VideophoneActivity.this.callInfo.setText(NVModel.CURR_ELEMENT.getName());
                ElementControl_VideophoneActivity.buttonAcceptMakeCall.setVisibility(0);
                ElementControl_VideophoneActivity.buttonDeclineCall.setVisibility(4);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$3$2 */
        class C19902 implements Runnable {
            C19902() {
            }

            public void run() {
                ElementControl_VideophoneActivity.this.callInfo.setText(NVModel.CURR_ELEMENT.getName());
                ElementControl_VideophoneActivity.buttonAcceptMakeCall.setVisibility(0);
                ElementControl_VideophoneActivity.buttonDeclineCall.setVisibility(4);
            }
        }

        C19913() {
        }

        public void onClick(View v) {
            if (VoipRRService.callInProgress) {
                VoipRRService.callInProgress = false;
                VoipRRService.incomingCall = false;
                new endCall().execute(new Void[0]);
                if (LinphoneMiniManager.getInstance() != null) {
                    LinphoneMiniManager.getInstance().terminateCall();
                }
                ElementControl_VideophoneActivity.handler.post(new C19891());
            } else if (VoipRRService.incomingCall) {
                VoipRRService.callInProgress = false;
                VoipRRService.incomingCall = false;
                if (VoipRRService.f284r != null && VoipRRService.f284r.isPlaying()) {
                    VoipRRService.f284r.stop();
                }
                ElementControl_VideophoneActivity.handler.post(new C19902());
                if (LinphoneMiniManager.getInstance() != null && LinphoneMiniManager.getLc().isIncall()) {
                    LinphoneMiniManager.getInstance().terminateCall();
                }
                ElementControl_VideophoneActivity.this.finish();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$4 */
    class C19924 implements OnClickListener {
        C19924() {
        }

        public void onClick(View v) {
            ElementControl_VideophoneActivity.speakerOn = !ElementControl_VideophoneActivity.speakerOn;
            ((AudioManager) ElementControl_VideophoneActivity.this.getSystemService("audio")).setSpeakerphoneOn(ElementControl_VideophoneActivity.speakerOn);
            ElementControl_VideophoneActivity.this.refreshSpeakerIcon();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$5 */
    class C19935 implements OnClickListener {
        C19935() {
        }

        public void onClick(View v) {
            boolean z;
            boolean z2 = true;
            if (ElementControl_VideophoneActivity.micOn) {
                z = false;
            } else {
                z = true;
            }
            ElementControl_VideophoneActivity.micOn = z;
            AudioManager am = (AudioManager) ElementControl_VideophoneActivity.this.getSystemService("audio");
            if (ElementControl_VideophoneActivity.micOn) {
                z2 = false;
            }
            am.setMicrophoneMute(z2);
            ElementControl_VideophoneActivity.this.refreshMicIcon();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$6 */
    class C19946 implements OnClickListener {
        C19946() {
        }

        public void onClick(View v) {
            Log.d(ElementControl_VideophoneActivity.LOG_TAG, "klikam");
            new gateRequest().execute(new Void[0]);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$7 */
    class C19957 implements Runnable {
        C19957() {
        }

        public void run() {
            ElementControl_VideophoneActivity.this.callInfo.setText(NVModel.CURR_ELEMENT.getName());
            ElementControl_VideophoneActivity.buttonAcceptMakeCall.setVisibility(0);
            ElementControl_VideophoneActivity.buttonDeclineCall.setVisibility(4);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$8 */
    class C19968 implements Runnable {
        C19968() {
        }

        public void run() {
            ElementControl_VideophoneActivity.this.buttonOpenGate.setImageResource(R.drawable.ic_lock_opened);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$9 */
    class C19979 implements Runnable {
        C19979() {
        }

        public void run() {
            ElementControl_VideophoneActivity.this.buttonOpenGate.setImageResource(R.drawable.ic_lock_closed);
        }
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$DoRead$1 */
        class C19981 implements Runnable {
            C19981() {
            }

            public void run() {
                ElementControl_VideophoneActivity.this.mjpegInfo.setVisibility(8);
                ElementControl_VideophoneActivity.this.mjpegView.setVisibility(0);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$DoRead$2 */
        class C19992 implements Runnable {
            C19992() {
            }

            public void run() {
                ElementControl_VideophoneActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_VideophoneActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_VideophoneActivity.this.mjpegView.setVisibility(4);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$DoRead$3 */
        class C20003 implements Runnable {
            C20003() {
            }

            public void run() {
                ElementControl_VideophoneActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_VideophoneActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_VideophoneActivity.this.mjpegView.setVisibility(4);
            }
        }

        protected MjpegInputStream doInBackground(String... _url) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            HttpConnectionParams.setSoTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            Log.e(ElementControl_VideophoneActivity.LOG_TAG, "DO_READ (VISIBLE=" + ElementControl_VideophoneActivity.VISIBLE + ")");
            while (ElementControl_VideophoneActivity.VISIBLE) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse res = httpclient.execute(new HttpGet(URI.create(_url[0].replaceAll("\\s", "") + ":8888")));
                    if (res.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                        Log.e(ElementControl_VideophoneActivity.LOG_TAG, "ERR 401");
                        return null;
                    }
                    Log.e(ElementControl_VideophoneActivity.LOG_TAG, "res=" + res.getStatusLine().getStatusCode());
                    ElementControl_VideophoneActivity.handler.post(new C19981());
                    return new MjpegInputStream(res.getEntity().getContent());
                } catch (ClientProtocolException e2) {
                    e2.printStackTrace();
                    Log.e(ElementControl_VideophoneActivity.LOG_TAG, "Request failed-ClientProtocolException", e2);
                    ElementControl_VideophoneActivity.handler.post(new C19992());
                } catch (IOException e3) {
                    e3.printStackTrace();
                    Log.e(ElementControl_VideophoneActivity.LOG_TAG, "Request failed-IOException", e3);
                    ElementControl_VideophoneActivity.handler.post(new C20003());
                }
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            ElementControl_VideophoneActivity.this.mjpegView.setSource(result);
            if (result != null) {
                result.setSkip(1);
            }
            ElementControl_VideophoneActivity.this.mjpegView.setDisplayMode(5);
            ElementControl_VideophoneActivity.this.mjpegView.showFps(true);
        }
    }

    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            ElementControl_VideophoneActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
            ElementControl_VideophoneActivity.this.startActivity(new Intent(ElementControl_VideophoneActivity.this, ElementControl_VideophoneActivity.class));
        }
    }

    public class cmdHttp extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... _url) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            HttpConnectionParams.setSoTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            try {
                HttpResponse res = httpclient.execute(new HttpGet(URI.create("http://" + _url[0].replaceAll("\\s", ""))));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e(ElementControl_VideophoneActivity.LOG_TAG, "Request failed-ClientProtocolException", e);
            } catch (IOException e2) {
                e2.printStackTrace();
                Log.e(ElementControl_VideophoneActivity.LOG_TAG, "Request failed-IOException", e2);
            }
            return null;
        }
    }

    public class endCall extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VoipRRService.connect("bla", "1026", ElementControl_VideophoneActivity.context, ElementControl_VideophoneActivity.handler);
            try {
                VoipRRService.send("GET /?streamStop");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VoipRRService.disconnect(ElementControl_VideophoneActivity.context, ElementControl_VideophoneActivity.handler);
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public class gateRequest extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VoipRRService.connect("bla", "1026", ElementControl_VideophoneActivity.context, ElementControl_VideophoneActivity.handler);
            try {
                VoipRRService.send("GET /?gateRequest");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VoipRRService.disconnect(ElementControl_VideophoneActivity.context, ElementControl_VideophoneActivity.handler);
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public class makeCall extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            VoipRRService.connect("bla", "1026", ElementControl_VideophoneActivity.context, ElementControl_VideophoneActivity.handler);
            try {
                VoipRRService.send("GET /?streamStart");
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            VoipRRService.disconnect(ElementControl_VideophoneActivity.context, ElementControl_VideophoneActivity.handler);
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        getWindow().addFlags(6815872);
        setContentView(R.layout.activity_elementcontrol_videophone);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        XMLProject.initModel(context);
        if (NVModel.CURR_ELEMENT == null || !(NVModel.CURR_ELEMENT instanceof VideophoneIP)) {
            ArrayList<IElement> vidips = NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE);
            if (vidips.size() < 1) {
                Log.d("Testowanie", "0");
                return;
            }
            NVModel.CURR_ELEMENT = (IElement) vidips.get(0);
        }
        this.width = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().x;
        this.height = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().y;
        this.url = Uri.decode("http://" + ((VideophoneIP) NVModel.CURR_ELEMENT).getAddress());
        Log.e("EC_VideophoneIP", "url=" + ((VideophoneIP) NVModel.CURR_ELEMENT).getAddress() + "[" + this.url + "]");
        if (this.width < 1 || this.height < 1 || this.url == null || this.url.equals("")) {
            Log.e(LOG_TAG, "Bad width/height/url!");
            return;
        }
        this.callInfo = (TextView) findViewById(R.id.callInfo);
        this.callInfo.setText(NVModel.CURR_ELEMENT.getName());
        this.mjpegInfo = (TextView) findViewById(R.id.mjpegInfo);
        this.mjpegFreezed = (LinearLayout) findViewById(R.id.mjpegFreezed);
        this.mjpegFreezed.setVisibility(8);
        this.mjpegView = (MjpegView) findViewById(R.id.mv);
        if (this.mjpegView != null) {
            this.mjpegView.setResolution(this.width, this.height);
            this.mjpegView.setOnClickListener(new C19861());
        }
        this.edittextCallAddress = (EditText) findViewById(R.id.edittextCallAddress);
        this.edittextCallAddress.setVisibility(4);
        buttonAcceptMakeCall = (SquareImageButton) findViewById(R.id.buttonAcceptMakeCall);
        buttonAcceptMakeCall.setOnClickListener(new C19882());
        buttonDeclineCall = (SquareImageButton) findViewById(R.id.buttonDeclineCall);
        buttonDeclineCall.setOnClickListener(new C19913());
        this.buttonSpeakerOnOff = (SquareImageButton) findViewById(R.id.buttonSpeakerOnOff);
        this.buttonSpeakerOnOff.setOnClickListener(new C19924());
        this.buttonMicOnOff = (SquareImageButton) findViewById(R.id.buttonMicOnOff);
        this.buttonMicOnOff.setOnClickListener(new C19935());
        this.buttonOpenGate = (SquareImageButton) findViewById(R.id.buttonOpenGate);
        this.buttonOpenGate.setImageResource(R.drawable.ic_lock_closed);
        this.buttonOpenGate.setOnClickListener(new C19946());
        this.proximitySensorAffected = false;
        this.mySensorManager = (SensorManager) getSystemService("sensor");
        this.myProximitySensor = this.mySensorManager.getDefaultSensor(8);
        if (this.myProximitySensor == null) {
            Log.d(LOG_TAG, "No Proximity Sensor!");
        } else {
            Log.d(LOG_TAG, this.myProximitySensor.getName());
            Log.d(LOG_TAG, "Maximum Range: " + String.valueOf(this.myProximitySensor.getMaximumRange()));
        }
        int powerValue = 32;
        try {
            powerValue = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable th) {
        }
        this.mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(powerValue, getLocalClassName());
        this.mMediaButtonReceiver = new CallButtonIntentReceiver();
        IntentFilter mediaFilter = new IntentFilter("android.intent.action.MEDIA_BUTTON");
        mediaFilter.setPriority(1000);
        registerReceiver(this.mMediaButtonReceiver, mediaFilter);
    }

    protected void sleepScreen(boolean on) {
        if (on) {
            if (!this.mWakeLock.isHeld()) {
                this.mWakeLock.acquire();
            }
            onPauseSetting = false;
            return;
        }
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        onPauseSetting = true;
    }

    public static Context getContext() {
        return context;
    }

    private void startVideoStream() {
        if (this.mjpegView != null) {
            Log.d(LOG_TAG, "startVideoStream");
            if (NVModel.CURR_ELEMENT == null || !(NVModel.CURR_ELEMENT instanceof VideophoneIP)) {
                ArrayList<IElement> vidips = NVModel.getElementsByType(NVModel.EL_TYPE_VIDEOPHONE);
                if (vidips.size() >= 1) {
                    NVModel.CURR_ELEMENT = (IElement) vidips.get(0);
                } else {
                    return;
                }
            }
            Log.e(LOG_TAG, "startVideoStream1 (VISIBLE=" + VISIBLE + ")");
            this.width = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().x;
            this.height = ((VideophoneIP) NVModel.CURR_ELEMENT).getSize().y;
            this.url = Uri.decode("http://" + ((VideophoneIP) NVModel.CURR_ELEMENT).getAddress());
            Log.d(LOG_TAG, "url=" + this.url);
            if (this.width >= 1 && this.height >= 1 && this.url != null && !this.url.equals("")) {
                Log.e(LOG_TAG, "startVideoStream2 (VISIBLE=" + VISIBLE + ")");
                this.mjpegView.setResolution(this.width, this.height);
                if (this.url != null && !this.url.isEmpty()) {
                    Log.e(LOG_TAG, "startVideoStream3 (VISIBLE=" + VISIBLE + ")");
                    new DoRead().execute(new String[]{this.url});
                    this.mjpegView.startPlayback();
                    Log.d(LOG_TAG, "Video stream STARTED");
                }
            }
        }
    }

    private void stopVideoStream() {
        if (this.mjpegView != null) {
            Log.d("EC_VideophoneActivity", "stopVideoStream");
            if (this.mjpegView.isStreaming()) {
                this.mjpegView.stopPlayback();
            }
            this.mjpegView.freeCameraMemory();
            Log.d("EC_VideophoneActivity", "Video stream STOPPED");
        }
    }

    private void refreshSpeakerIcon() {
        this.buttonSpeakerOnOff.setImageResource(speakerOn ? R.drawable.ic_speaker_on : R.drawable.ic_speaker_off);
    }

    private void refreshMicIcon() {
        this.buttonMicOnOff.setImageResource(micOn ? R.drawable.ic_mic_on : R.drawable.ic_mic_off);
    }

    public void onResume() {
        boolean z = true;
        super.onResume();
        Log.e(LOG_TAG, "onResume1 (VISIBLE=" + VISIBLE + ")");
        VISIBLE = true;
        this.mySensorManager.registerListener(this, this.myProximitySensor, 3);
        registerReceiver(this.myReceiver, new IntentFilter("android.intent.action.VOICE_COMMAND"));
        if (!this.proximitySensorAffected) {
            this.proximitySensorAffected = false;
            if (LinphoneMiniManager.getInstance() != null) {
                if (LinphoneMiniManager.getLc().isIncall()) {
                    LinphoneMiniManager.getInstance().terminateCall();
                }
                LinphoneMiniManager.getInstance().destroy();
            }
            LinphoneMiniManager linphoneMiniManager = new LinphoneMiniManager(context);
            VoipRRService.vidipActivity = this;
            this.edittextCallAddress.setText(VoipRRService.sip_proxy);
            buttonAcceptMakeCall.setVisibility(0);
            if (VoipRRService.incomingCall) {
                buttonDeclineCall.setVisibility(0);
            } else {
                buttonDeclineCall.setVisibility(4);
            }
            AudioManager am = (AudioManager) getSystemService("audio");
            am.setMicrophoneMute(false);
            am.setMode(0);
            speakerOn = am.isSpeakerphoneOn();
            refreshSpeakerIcon();
            if (am.isMicrophoneMute()) {
                z = false;
            }
            micOn = z;
            refreshMicIcon();
        }
        Log.d("EC_VideophoneActivity", "Echo Canceller: " + Hacks.hasBuiltInEchoCanceller());
        Log.e(LOG_TAG, "onResume2 (VISIBLE=" + VISIBLE + ")");
        startVideoStream();
    }

    public void onStart() {
        VISIBLE = true;
        super.onStart();
    }

    public void onPause() {
        VISIBLE = false;
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        unregisterReceiver(this.myReceiver);
        this.mySensorManager.unregisterListener(this);
        VoipRRService.vidipActivity = null;
        stopVideoStream();
        if (!(this.proximitySensorAffected || LinphoneMiniManager.getInstance() == null)) {
            if (LinphoneMiniManager.getLc().isIncall()) {
                LinphoneMiniManager.getInstance().terminateCall();
                breakCall();
            }
            if (LinphoneMiniManager.getInstance() != null) {
                LinphoneMiniManager.getInstance().destroy();
            }
        }
        if (onPauseSetting) {
            Log.d(LOG_TAG, "onPause(streamStop)");
            if (VoipRRService.callInProgress) {
                new endCall().execute(new Void[0]);
            }
            if (LinphoneMiniManager.getInstance() != null) {
                LinphoneMiniManager.getInstance().terminateCall();
                LinphoneMiniManager.getInstance().destroy();
            }
            if (VoipRRService.f284r != null && VoipRRService.f284r.isPlaying()) {
                VoipRRService.f284r.stop();
            }
            VoipRRService.callInProgress = false;
            VoipRRService.incomingCall = false;
        }
    }

    public void onDestroy() {
        VISIBLE = false;
        unregisterReceiver(this.mMediaButtonReceiver);
        Log.d(LOG_TAG, "onDestroy()");
        stopVideoStream();
        Log.d(LOG_TAG, "onDestroy(streamStop)");
        if (VoipRRService.callInProgress) {
            new endCall().execute(new Void[0]);
        }
        if (LinphoneMiniManager.getInstance() != null) {
            LinphoneMiniManager.getInstance().terminateCall();
            LinphoneMiniManager.getInstance().destroy();
        }
        if (VoipRRService.f284r != null && VoipRRService.f284r.isPlaying()) {
            VoipRRService.f284r.stop();
        }
        VoipRRService.callInProgress = false;
        VoipRRService.incomingCall = false;
        super.onDestroy();
    }

    public void cancelCall() {
        Log.d(LOG_TAG, "cancelCall(streamStop)");
        if (VoipRRService.callInProgress) {
            new endCall().execute(new Void[0]);
        }
        if (LinphoneMiniManager.getInstance() != null) {
            LinphoneMiniManager.getInstance().terminateCall();
            LinphoneMiniManager.getInstance().destroy();
        }
        if (VoipRRService.f284r != null && VoipRRService.f284r.isPlaying()) {
            VoipRRService.f284r.stop();
        }
        handler.post(new C19957());
        VoipRRService.callInProgress = false;
        VoipRRService.incomingCall = false;
    }

    public void breakCall() {
        cancelCall();
    }

    public void gateOpen() {
        handler.post(new C19968());
        handler.postDelayed(new C19979(), 5000);
    }

    public void gateClose() {
        handler.post(new Runnable() {
            public void run() {
                ElementControl_VideophoneActivity.this.buttonOpenGate.setImageResource(R.drawable.ic_lock_closed);
            }
        });
    }

    public void takeSnapshot() {
        LinphoneMiniManager.getInstance().startEchoCalibration();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    this.width = data.getIntExtra("width", this.width);
                    this.height = data.getIntExtra("height", this.height);
                    this.url = data.getStringExtra(PlusShare.KEY_CALL_TO_ACTION_URL);
                    if (this.mjpegView != null) {
                        this.mjpegView.setResolution(this.width, this.height);
                    }
                    Editor editor = getSharedPreferences("SAVED_VALUES", 0).edit();
                    editor.putInt("width", this.width);
                    editor.putInt("height", this.height);
                    editor.putString(PlusShare.KEY_CALL_TO_ACTION_URL, this.url);
                    editor.commit();
                    new RestartApp().execute(new Void[0]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setVideoStreamError(boolean error) {
        this.videoStreamError = error;
        if (this.videoStreamError) {
            new Thread() {

                /* renamed from: eu.nexwell.android.nexovision.ElementControl_VideophoneActivity$11$1 */
                class C19851 implements Runnable {
                    C19851() {
                    }

                    public void run() {
                        ElementControl_VideophoneActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                        ElementControl_VideophoneActivity.this.mjpegInfo.setVisibility(0);
                        ElementControl_VideophoneActivity.this.mjpegView.setVisibility(4);
                    }
                }

                public void run() {
                    ElementControl_VideophoneActivity.this.stopVideoStream();
                    ElementControl_VideophoneActivity.handler.post(new C19851());
                    ElementControl_VideophoneActivity.this.startVideoStream();
                }
            }.start();
        } else {
            handler.post(new Runnable() {
                public void run() {
                    ElementControl_VideophoneActivity.this.mjpegInfo.setVisibility(8);
                    ElementControl_VideophoneActivity.this.mjpegView.setVisibility(0);
                }
            });
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != 8) {
            return;
        }
        if (event.values[0] == 0.0f) {
            this.proximitySensorAffected = true;
            sleepScreen(true);
            return;
        }
        sleepScreen(false);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(LOG_TAG, "INTENT_ACTION=" + intent.getAction());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("LOG_TAG", keyCode + "");
        Log.d("LOG_TAG", event + "");
        return super.onKeyDown(keyCode, event);
    }
}
