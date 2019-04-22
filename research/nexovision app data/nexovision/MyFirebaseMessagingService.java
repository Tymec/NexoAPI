package eu.nexwell.android.nexovision;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.linphone.mini.VidSockets;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private Handler handler;

    /* renamed from: eu.nexwell.android.nexovision.MyFirebaseMessagingService$1 */
    class C20351 implements Runnable {
        C20351() {
        }

        public void run() {
            if (!ElementControl_VidIPActivity.isVisible) {
                Intent intent = new Intent().setClass(MyFirebaseMessagingService.this, ElementControl_VidIPActivity.class);
                intent.addFlags(DriveFile.MODE_READ_ONLY);
                if (VidSockets.incomingCall && !VidSockets.callInProgress) {
                    intent.putExtra("buttonDecline", true);
                }
                MyFirebaseMessagingService.this.startActivity(intent);
            } else if (VidSockets.incomingCall && !VidSockets.callInProgress) {
                ElementControl_VidIPActivity.buttonDeclineCall.setVisibility(0);
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        this.handler = new Handler();
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            forwardCall();
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void forwardCall() {
        if (!VidSockets.callInProgress && !VidSockets.incomingCall) {
            Uri ringtone = RingtoneManager.getDefaultUri(1);
            if (ringtone != null) {
                VidSockets.f283r = RingtoneManager.getRingtone(this, ringtone);
                if (!(VidSockets.f283r == null || VidSockets.f283r.isPlaying())) {
                    VidSockets.f283r.play();
                }
            }
            VidSockets.incomingCall = true;
            this.handler.post(new C20351());
        }
    }
}
