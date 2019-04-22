package eu.nexwell.android.nexovision.communication;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import eu.nexwell.android.nexovision.MainActivity;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISwitch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class NexoService extends Service implements NexoTalkListener {
    private static boolean cipher;
    private static Context context = null;
    private static Handler handler;
    private static String ip;
    private static ServiceConnection mConnection = new C21051();
    private static String password;
    private static String port;
    private static Ringtone ringtone;
    private static NexoRunnable runnable;
    private static Thread th;
    private final IBinder mBinder = new LocalBinder();

    /* renamed from: eu.nexwell.android.nexovision.communication.NexoService$1 */
    static class C21051 implements ServiceConnection {
        C21051() {
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            NexoService.context = ((LocalBinder) service).getServiceInstance().getApplicationContext();
            Log.d("NexoService", "THREAD START");
            NexoService.th = new Thread(NexoService.runnable = new NexoRunnable(NexoService.ip, NexoService.port, NexoService.password, NexoService.cipher));
            NexoService.th.start();
        }

        public void onServiceDisconnected(ComponentName arg0) {
        }
    }

    public class LocalBinder extends Binder {
        public NexoService getServiceInstance() {
            return NexoService.this;
        }
    }

    public static Context getContext() {
        return context;
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        Log.d("NexoService", "BIND");
        return this.mBinder;
    }

    public void onCreate() {
        Log.d("NexoService", "CREATE");
        NexoTalk.addNexoTalkListener(this);
    }

    public void onDestroy() {
        Log.d("NexoService", "DESTROY");
        NexoTalk.removeNexoTalkListener(this);
        runnable.finish();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runnable = null;
        th = null;
        context = null;
        super.onDestroy();
    }

    public void onStart(Intent intent, int startId) {
        Log.d("NexoService", "START");
        handler = new Handler();
        super.onStart(intent, startId);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NexoService", "START");
        handler = new Handler();
        return 3;
    }

    public static void start(Activity act) {
        act.startService(new Intent(MainActivity.getContext(), NexoService.class));
        act.bindService(new Intent(MainActivity.getContext(), NexoService.class), mConnection, 1);
    }

    public static void stop(Activity act) {
        try {
            act.unbindService(mConnection);
        } catch (Exception e) {
            Log.e("NexoService", "Cannot UNBIND service!");
        }
        try {
            act.stopService(new Intent(MainActivity.getContext(), NexoService.class));
        } catch (Exception e2) {
            Log.e("NexoService", "Cannot STOP service!");
        }
    }

    public static void goForeground(ArrayList<IElement> els) {
        if (runnable != null) {
            runnable.goForeground(els);
        }
    }

    public static void goForeground(IElement el) {
        if (runnable != null) {
            ArrayList<IElement> els = new ArrayList();
            els.add(el);
            runnable.goForeground(els);
        }
    }

    public static void goBackground(ArrayList<IElement> els) {
        if (runnable != null) {
            runnable.goBackground(els);
        }
    }

    public static void goBackground() {
        if (runnable != null) {
            runnable.goBackground(null);
        }
    }

    public static void freeze() {
        if (runnable != null) {
            runnable.freeze();
        }
    }

    public static void doImport(ArrayList<Integer> types) {
        if (runnable != null) {
            runnable.doImport(types);
        }
    }

    public static ArrayList<Integer> getAlarming() {
        if (runnable != null) {
            return runnable.getAlarming();
        }
        return null;
    }

    public static TreeMap<Integer, ArrayList<String>> getImportedResources() {
        if (runnable != null) {
            return runnable.getSwitchesList();
        }
        return null;
    }

    public static HashMap<String, ArrayList<String>> getImportedResourcesData() {
        if (runnable != null) {
            return runnable.getSwitchesDataList();
        }
        return null;
    }

    public static void queueAction(String action) {
        if (runnable != null) {
            runnable.queueAction(action, null);
        }
    }

    public static void queueActionAndUpdate(ISwitch sw, String action) {
        if (runnable != null) {
            runnable.queueAction(action, sw);
        }
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
                    NexoService.stopRingtone();
                    return;
                }
                Log.e("InBkgListenerService", "!!! ALARM(" + el.getName() + ") !!!");
                Uri alarm = RingtoneManager.getDefaultUri(4);
                if (alarm != null) {
                    NexoService.ringtone = RingtoneManager.getRingtone(NexoService.getContext(), alarm);
                    if (!(NexoService.ringtone == null || NexoService.ringtone.isPlaying())) {
                        NexoService.ringtone.play();
                    }
                }
                if (!MainActivity.VISIBLE) {
                    Intent intent = new Intent().setClass(NexoService.getContext(), MainActivity.class);
                    intent.addFlags(DriveFile.MODE_READ_ONLY);
                    NexoService.this.startActivity(intent);
                }
            }
        });
    }

    public void connectionStatus(boolean connected) {
    }

    public void connectionProcessInfo(String info, String error) {
    }
}
