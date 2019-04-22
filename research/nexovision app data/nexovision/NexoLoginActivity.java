package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.IElement;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class NexoLoginActivity extends AppCompatActivity implements NexoTalkListener {
    private static Context context;
    public static Handler handler;
    private Button buttonLogin;
    private EditText inputIP;
    private EditText inputPasswd;

    /* renamed from: eu.nexwell.android.nexovision.NexoLoginActivity$1 */
    class C20361 implements OnClickListener {
        C20361() {
        }

        public void onClick(View v) {
            if (NexoTalk.isConnected()) {
                NexoService.stop((MainActivity) MainActivity.getContext());
            } else if (NexoLoginActivity.this.inputIP.getText().toString() != null && !NexoLoginActivity.this.inputIP.getText().toString().equals("") && NexoLoginActivity.this.inputPasswd.getText().toString() != null && !NexoLoginActivity.this.inputPasswd.getText().toString().equals("") && !NexoTalk.isConnected()) {
                String[] target = NexoLoginActivity.this.inputIP.getText().toString().split(":");
                if (target.length > 0 && target[0] != null && !target[0].equals("")) {
                    String port;
                    if (target.length <= 1 || target[1] == null || target[1].equals("")) {
                        port = "1024";
                    } else {
                        port = target[1];
                    }
                    Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext()).edit();
                    editor.putString("pref_rememberme_login_ip", NexoLoginActivity.this.inputIP.getText().toString());
                    editor.putString("pref_rememberme_login_passwd", NexoLoginActivity.this.inputPasswd.getText().toString());
                    editor.commit();
                    NexoService.start((MainActivity) MainActivity.getContext());
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        NexoTalk.addNexoTalkListener(this);
        setContentView(R.layout.activity_loginform);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.inputIP = (EditText) findViewById(R.id.input_ip);
        this.inputPasswd = (EditText) findViewById(R.id.input_passwd);
        this.buttonLogin = (Button) findViewById(R.id.button_login);
        if (NexoTalk.isConnected()) {
            this.inputIP.setEnabled(false);
            this.inputPasswd.setEnabled(false);
            this.buttonLogin.setText(R.string.NexoLoginActivity_LogoutButton);
        }
        this.buttonLogin.setOnClickListener(new C20361());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.inputIP.setText(sharedPref.getString("pref_rememberme_login_ip", ""));
        this.inputPasswd.setText(sharedPref.getString("pref_rememberme_login_passwd", ""));
    }

    protected void onDestroy() {
        NexoTalk.removeNexoTalkListener(this);
        super.onDestroy();
    }

    public static Context getContext() {
        return context;
    }

    public void onImport(int type, int iterator) {
    }

    public void onImportEnd(ArrayList<Integer> arrayList) {
    }

    public void onStatusUpdate(IElement el, boolean finish) {
    }

    public void onPartitionAlarm(IElement el) {
    }

    public void connectionStatus(final boolean connected) {
        handler.post(new Runnable() {
            public void run() {
                boolean z = true;
                NexoLoginActivity.this.inputIP.setEnabled(!connected);
                EditText access$100 = NexoLoginActivity.this.inputPasswd;
                if (connected) {
                    z = false;
                }
                access$100.setEnabled(z);
                NexoLoginActivity.this.buttonLogin.setText(connected ? R.string.NexoLoginActivity_LogoutButton : R.string.NexoLoginActivity_LoginButton);
            }
        });
        finish();
    }

    public void connectionProcessInfo(String info, String error) {
    }
}
