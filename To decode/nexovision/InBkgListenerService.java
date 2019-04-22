package eu.nexwell.android.nexovision;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.IElement;
import java.util.ArrayList;

public class InBkgListenerService extends Service implements NexoTalkListener {
    public static ArrayList<Integer> alarming = new ArrayList();
    public static Context context;
    public static Handler handler;
    public static boolean isRunning = false;
    public static Ringtone ringtone;

    /* renamed from: eu.nexwell.android.nexovision.InBkgListenerService$1 */
    class C20221 implements Runnable {
        C20221() {
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                Log.d("InBkgListenerS", "Thread(" + this + "):" + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.InBkgListenerService$2 */
    class C20232 implements Runnable {
        C20232() {
        }

        public void run() {
            Intent intent = new Intent().setClass(InBkgListenerService.this, MainActivity.class);
            intent.addFlags(DriveFile.MODE_READ_ONLY);
            InBkgListenerService.this.startActivity(intent);
        }
    }

    static {
        Log.d("InBkgListenerS", "static{}");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        context = this;
        NexoTalk.addNexoTalkListener(this);
    }

    public void onDestroy() {
        Log.d("InBkgListenerS", "STOP");
        stopRingtone();
        NexoTalk.removeNexoTalkListener(this);
        isRunning = false;
        super.onDestroy();
    }

    public void onStart(Intent intent, int startid) {
        isRunning = true;
        handler = new Handler();
        Log.d("InBkgListenerS", "START");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        handler = new Handler();
        Log.d("InBkgListenerS", "START");
        new Thread(new C20221()).start();
        return 3;
    }

    private void partitionAlarming(IElement el) {
        handler.post(new C20232());
    }

    public void onImport(int type, int iterator) {
    }

    public void onImportEnd(ArrayList<Integer> arrayList) {
    }

    public void onStatusUpdate(IElement el, boolean finish) {
    }

    public static void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    public void onPartitionAlarm(final IElement el) {
        handler.post(new Runnable() {
            public void run() {
                if (el == null) {
                    Log.e("InBkgListenerService", "!!! STOP ALARM !!!");
                    InBkgListenerService.stopRingtone();
                    return;
                }
                Log.e("InBkgListenerService", "!!! ALARM(" + el.getName() + ") !!!");
                Uri alarm = RingtoneManager.getDefaultUri(4);
                if (alarm != null) {
                    InBkgListenerService.ringtone = RingtoneManager.getRingtone(InBkgListenerService.context, alarm);
                    if (!(InBkgListenerService.ringtone == null || InBkgListenerService.ringtone.isPlaying())) {
                        InBkgListenerService.ringtone.play();
                    }
                }
                if (!MainActivity.VISIBLE) {
                    Intent intent = new Intent().setClass(InBkgListenerService.context, MainActivity.class);
                    intent.addFlags(DriveFile.MODE_READ_ONLY);
                    InBkgListenerService.this.startActivity(intent);
                }
            }
        });
    }

    public void connectionStatus(boolean connected) {
    }

    public void connectionProcessInfo(String info, String error) {
    }
}
