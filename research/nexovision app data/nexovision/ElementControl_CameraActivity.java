package eu.nexwell.android.nexovision;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.camera.simplemjpeg.MjpegInputStream;
import com.camera.simplemjpeg.MjpegView;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.search.SearchAuth.StatusCodes;
import eu.nexwell.android.nexovision.model.CameraIP;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
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

public class ElementControl_CameraActivity extends AppCompatActivity {
    private static String LOG_TAG = "EC_CameraActivity";
    private static final int REQUEST_SETTINGS = 0;
    public static boolean VISIBLE;
    public static ProgressDialog connectDialog;
    private static Context context;
    private static Handler handler;
    private TextView cameraInfo = null;
    private int height;
    private LinearLayout mjpegFreezed = null;
    private TextView mjpegInfo = null;
    private MjpegView mjpegView = null;
    private String url;
    private boolean videoStreamError = false;
    private ViewTreeObserver vto;
    private int width;

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$1 */
    class C19361 implements OnClickListener {
        C19361() {
        }

        public void onClick(View v) {
            if (ElementControl_CameraActivity.this.mjpegView.isFreezedPlayback()) {
                ElementControl_CameraActivity.this.mjpegView.unfreezePlayback();
                ElementControl_CameraActivity.this.mjpegFreezed.setVisibility(8);
                return;
            }
            ElementControl_CameraActivity.this.mjpegView.freezePlayback();
            LayoutParams lp = (LayoutParams) ElementControl_CameraActivity.this.mjpegFreezed.getLayoutParams();
            lp.setMargins(0, 0, 0, ElementControl_CameraActivity.this.mjpegView.getMeasuredHeight() / 6);
            ElementControl_CameraActivity.this.mjpegFreezed.setLayoutParams(lp);
            ElementControl_CameraActivity.this.mjpegFreezed.setVisibility(0);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$2 */
    class C19382 extends Thread {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$2$1 */
        class C19371 implements Runnable {
            C19371() {
            }

            public void run() {
                ElementControl_CameraActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_CameraActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_CameraActivity.this.mjpegView.setVisibility(4);
            }
        }

        C19382() {
        }

        public void run() {
            ElementControl_CameraActivity.this.stopVideoStream();
            ElementControl_CameraActivity.handler.post(new C19371());
            ElementControl_CameraActivity.this.startVideoStream();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$3 */
    class C19393 implements Runnable {
        C19393() {
        }

        public void run() {
            ElementControl_CameraActivity.this.mjpegInfo.setVisibility(8);
            ElementControl_CameraActivity.this.mjpegView.setVisibility(0);
        }
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$DoRead$1 */
        class C19401 implements Runnable {
            C19401() {
            }

            public void run() {
                ElementControl_CameraActivity.this.mjpegInfo.setVisibility(8);
                ElementControl_CameraActivity.this.mjpegView.setVisibility(0);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$DoRead$2 */
        class C19412 implements Runnable {
            C19412() {
            }

            public void run() {
                ElementControl_CameraActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_CameraActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_CameraActivity.this.mjpegView.setVisibility(4);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_CameraActivity$DoRead$3 */
        class C19423 implements Runnable {
            C19423() {
            }

            public void run() {
                ElementControl_CameraActivity.this.mjpegInfo.setText(R.string.ELCVideophoneActivity_VideoError);
                ElementControl_CameraActivity.this.mjpegInfo.setVisibility(0);
                ElementControl_CameraActivity.this.mjpegView.setVisibility(4);
            }
        }

        protected MjpegInputStream doInBackground(String... _url) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            HttpConnectionParams.setSoTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            Log.e(ElementControl_CameraActivity.LOG_TAG, "DO_READ (VISIBLE=" + ElementControl_CameraActivity.VISIBLE + ")");
            while (ElementControl_CameraActivity.VISIBLE) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse res = httpclient.execute(new HttpGet(URI.create(_url[0].replaceAll("\\s", ""))));
                    if (res.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                        Log.e(ElementControl_CameraActivity.LOG_TAG, "ERR 401");
                        return null;
                    }
                    Log.e(ElementControl_CameraActivity.LOG_TAG, "res=" + res.getStatusLine().getStatusCode());
                    ElementControl_CameraActivity.handler.post(new C19401());
                    return new MjpegInputStream(res.getEntity().getContent());
                } catch (ClientProtocolException e2) {
                    e2.printStackTrace();
                    Log.e(ElementControl_CameraActivity.LOG_TAG, "Request failed-ClientProtocolException", e2);
                    ElementControl_CameraActivity.handler.post(new C19412());
                } catch (IOException e3) {
                    e3.printStackTrace();
                    Log.e(ElementControl_CameraActivity.LOG_TAG, "Request failed-IOException", e3);
                    ElementControl_CameraActivity.handler.post(new C19423());
                }
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            ElementControl_CameraActivity.this.mjpegView.setSource(result);
            if (result != null) {
                result.setSkip(1);
            }
            ElementControl_CameraActivity.this.mjpegView.setDisplayMode(5);
            ElementControl_CameraActivity.this.mjpegView.showFps(true);
        }
    }

    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            ElementControl_CameraActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
            ElementControl_CameraActivity.this.startActivity(new Intent(ElementControl_CameraActivity.this, ElementControl_CameraActivity.class));
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
                Log.e(ElementControl_CameraActivity.LOG_TAG, "Request failed-ClientProtocolException", e);
            } catch (IOException e2) {
                e2.printStackTrace();
                Log.e(ElementControl_CameraActivity.LOG_TAG, "Request failed-IOException", e2);
            }
            return null;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        getWindow().addFlags(6815872);
        setContentView(R.layout.activity_elementcontrol_camera);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (NVModel.CURR_ELEMENT == null || !(NVModel.CURR_ELEMENT instanceof CameraIP)) {
            ArrayList<IElement> cameras = NVModel.getElementsByType(NVModel.EL_TYPE_CAMERA);
            if (cameras.size() >= 1) {
                NVModel.CURR_ELEMENT = (IElement) cameras.get(0);
            } else {
                return;
            }
        }
        this.width = ((CameraIP) NVModel.CURR_ELEMENT).getSize().x;
        this.height = ((CameraIP) NVModel.CURR_ELEMENT).getSize().y;
        this.url = Uri.decode("http://" + ((CameraIP) NVModel.CURR_ELEMENT).getAddress());
        Log.e("EC_CameraIP", "url=" + ((CameraIP) NVModel.CURR_ELEMENT).getAddress() + "[" + this.url + "]");
        if (this.width < 1 || this.height < 1 || this.url == null || this.url.equals("")) {
            Log.e(LOG_TAG, "Bad width/height/url!");
            return;
        }
        this.cameraInfo = (TextView) findViewById(R.id.cameraInfo);
        this.cameraInfo.setText(NVModel.CURR_ELEMENT.getName());
        this.mjpegInfo = (TextView) findViewById(R.id.mjpegInfo);
        this.mjpegFreezed = (LinearLayout) findViewById(R.id.mjpegFreezed);
        this.mjpegFreezed.setVisibility(8);
        this.mjpegView = (MjpegView) findViewById(R.id.mv);
        if (this.mjpegView != null) {
            this.mjpegView.setResolution(this.width, this.height);
            this.mjpegView.setOnClickListener(new C19361());
        }
    }

    public static Context getContext() {
        return context;
    }

    private void startVideoStream() {
        if (this.mjpegView != null) {
            Log.d(LOG_TAG, "startVideoStream");
            if (NVModel.CURR_ELEMENT == null || !(NVModel.CURR_ELEMENT instanceof CameraIP)) {
                ArrayList<IElement> cameras = NVModel.getElementsByType(NVModel.EL_TYPE_CAMERA);
                if (cameras.size() >= 1) {
                    NVModel.CURR_ELEMENT = (IElement) cameras.get(0);
                } else {
                    return;
                }
            }
            Log.e(LOG_TAG, "startVideoStream1 (VISIBLE=" + VISIBLE + ")");
            this.width = ((CameraIP) NVModel.CURR_ELEMENT).getSize().x;
            this.height = ((CameraIP) NVModel.CURR_ELEMENT).getSize().y;
            this.url = Uri.decode("http://" + ((CameraIP) NVModel.CURR_ELEMENT).getAddress());
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
            Log.d("EC_CameraActivity", "stopVideoStream");
            if (this.mjpegView.isStreaming()) {
                this.mjpegView.stopPlayback();
            }
            this.mjpegView.freeCameraMemory();
            Log.d("EC_CameraActivity", "Video stream STOPPED");
        }
    }

    public void onResume() {
        super.onResume();
        VISIBLE = true;
        startVideoStream();
    }

    public void onStart() {
        VISIBLE = true;
        super.onStart();
    }

    public void onPause() {
        VISIBLE = false;
        super.onPause();
        stopVideoStream();
    }

    public void onDestroy() {
        VISIBLE = false;
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
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
            boolean unfreeze = false;
            try {
                FileOutputStream out2 = new FileOutputStream(path + File.separator + filename);
                try {
                    if (!this.mjpegView.isFreezedPlayback()) {
                        this.mjpegView.freezePlayback();
                        this.mjpegView.waitFreezed();
                        unfreeze = true;
                    }
                    this.mjpegView.getBitmap().compress(CompressFormat.JPEG, 80, out2);
                    if (out2 != null) {
                        try {
                            out2.close();
                            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(path + File.separator + filename).getAbsolutePath()}, null, null);
                            Snackbar.make(findViewById(R.id.container), "Snapshot saved to\n" + path + File.separator + filename, 0).show();
                            if (unfreeze) {
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
            new C19382().start();
        } else {
            handler.post(new C19393());
        }
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
}
