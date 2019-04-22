package eu.nexwell.android.nexovision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;
import org.linphone.mini.VoipRRService;

public class CallButtonIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        Toast.makeText(context, "BUTTON PRESSED? (" + intentAction + ")", 0).show();
        if ("android.intent.action.MEDIA_BUTTON".equals(intentAction)) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            if (event != null) {
                if (event.getAction() == 0 && ElementControl_VideophoneActivity.VISIBLE) {
                    if (VoipRRService.incomingCall) {
                        ElementControl_VideophoneActivity.buttonAcceptMakeCall.performClick();
                    } else if (VoipRRService.callInProgress) {
                        ElementControl_VideophoneActivity.buttonDeclineCall.performClick();
                    }
                }
                abortBroadcast();
            }
        }
    }
}
