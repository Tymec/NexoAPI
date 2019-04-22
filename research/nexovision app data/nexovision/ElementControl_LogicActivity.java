package eu.nexwell.android.nexovision;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Partition;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class ElementControl_LogicActivity extends AppCompatActivity implements NexoTalkListener {
    private static String LOG_TAG = "";
    private static Context context;
    private static Handler handler;
    private Partition PARTITION;
    private SquareButton buttonArm;
    private SquareButton buttonClear;
    private SquareButton buttonDisarm;
    private SquareButton buttonFail;
    private SquareButton buttonOk;
    private FrameLayout frameLayout;
    private SquareImageView imageButton;
    private EditText inputPasswd;
    private ViewTreeObserver vto;

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_LogicActivity$1 */
    class C19431 implements OnClickListener {
        C19431() {
        }

        public void onClick(View v) {
            if (ElementControl_LogicActivity.this.inputPasswd.getText().toString() != null && !ElementControl_LogicActivity.this.inputPasswd.getText().toString().isEmpty()) {
                NexoService.queueActionAndUpdate(ElementControl_LogicActivity.this.PARTITION, ElementControl_LogicActivity.this.PARTITION.clear(ElementControl_LogicActivity.this.inputPasswd.getText().toString()));
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_LogicActivity$2 */
    class C19442 implements OnClickListener {
        C19442() {
        }

        public void onClick(View v) {
            if (ElementControl_LogicActivity.this.inputPasswd.getText().toString() != null && !ElementControl_LogicActivity.this.inputPasswd.getText().toString().isEmpty()) {
                NexoService.queueActionAndUpdate(ElementControl_LogicActivity.this.PARTITION, ElementControl_LogicActivity.this.PARTITION.arm(ElementControl_LogicActivity.this.inputPasswd.getText().toString()));
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_LogicActivity$3 */
    class C19453 implements OnClickListener {
        C19453() {
        }

        public void onClick(View v) {
            if (ElementControl_LogicActivity.this.inputPasswd.getText().toString() != null && !ElementControl_LogicActivity.this.inputPasswd.getText().toString().isEmpty()) {
                NexoService.queueActionAndUpdate(ElementControl_LogicActivity.this.PARTITION, ElementControl_LogicActivity.this.PARTITION.disarm(ElementControl_LogicActivity.this.inputPasswd.getText().toString()));
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_LogicActivity$4 */
    class C19464 implements OnEditorActionListener {
        C19464() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && event.getKeyCode() == 66) || actionId == 6) {
                if (ElementControl_LogicActivity.this.buttonArm.getVisibility() == 0) {
                    ElementControl_LogicActivity.this.buttonArm.performClick();
                }
                if (ElementControl_LogicActivity.this.buttonDisarm.getVisibility() == 0) {
                    ElementControl_LogicActivity.this.buttonDisarm.performClick();
                }
                if (ElementControl_LogicActivity.this.buttonClear.getVisibility() == 0) {
                    ElementControl_LogicActivity.this.buttonClear.performClick();
                }
            }
            return false;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_LogicActivity$5 */
    class C19475 implements OnGlobalLayoutListener {
        C19475() {
        }

        public void onGlobalLayout() {
            int width = 10;
            if (ElementControl_LogicActivity.this.buttonClear.getVisibility() == 0) {
                width = ElementControl_LogicActivity.this.buttonClear.getMeasuredWidth();
                ElementControl_LogicActivity.this.buttonClear.setTextSize(0, (float) (width / 10));
                ElementControl_LogicActivity.this.buttonClear.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_LogicActivity.this.buttonDisarm.getVisibility() == 0) {
                width = ElementControl_LogicActivity.this.buttonDisarm.getMeasuredWidth();
                ElementControl_LogicActivity.this.buttonDisarm.setTextSize(0, (float) (width / 10));
                ElementControl_LogicActivity.this.buttonDisarm.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_LogicActivity.this.buttonArm.getVisibility() == 0) {
                width = ElementControl_LogicActivity.this.buttonArm.getMeasuredWidth();
                ElementControl_LogicActivity.this.buttonArm.setTextSize(0, (float) (width / 10));
                ElementControl_LogicActivity.this.buttonArm.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_LogicActivity.this.buttonOk.getVisibility() == 0) {
                width = ElementControl_LogicActivity.this.buttonOk.getMeasuredWidth();
                ElementControl_LogicActivity.this.buttonOk.setTextSize(0, (float) (width / 10));
                ElementControl_LogicActivity.this.buttonOk.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_LogicActivity.this.buttonFail.getVisibility() == 0) {
                width = ElementControl_LogicActivity.this.buttonFail.getMeasuredWidth();
                ElementControl_LogicActivity.this.buttonFail.setTextSize(0, (float) (width / 10));
                ElementControl_LogicActivity.this.buttonFail.setPadding(0, 0, 0, width / 8);
            }
            LayoutParams lp = (LayoutParams) ElementControl_LogicActivity.this.imageButton.getLayoutParams();
            lp.width = (width / 3) * 2;
            lp.height = lp.width;
            lp.setMargins(0, -(width / 24), 0, width / 24);
            ElementControl_LogicActivity.this.imageButton.setLayoutParams(lp);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        LOG_TAG = getClass().getName();
        setContentView(R.layout.activity_elementcontrol_partition);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (!(NVModel.CURR_ELEMENT instanceof Partition)) {
            finish();
        }
        this.PARTITION = (Partition) NVModel.CURR_ELEMENT;
        NexoTalk.addNexoTalkListener(this);
        this.frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        this.imageButton = (SquareImageView) findViewById(R.id.imageButton);
        this.buttonFail = (SquareButton) findViewById(R.id.buttonFail);
        this.buttonOk = (SquareButton) findViewById(R.id.buttonOk);
        this.buttonClear = (SquareButton) findViewById(R.id.buttonClear);
        this.buttonClear.setOnClickListener(new C19431());
        this.buttonArm = (SquareButton) findViewById(R.id.buttonArm);
        this.buttonArm.setOnClickListener(new C19442());
        this.buttonDisarm = (SquareButton) findViewById(R.id.buttonDisarm);
        this.buttonDisarm.setOnClickListener(new C19453());
        this.buttonFail.setVisibility(8);
        this.buttonOk.setVisibility(8);
        if (this.PARTITION.isAlarming()) {
            this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_ring, null));
            this.buttonClear.setVisibility(0);
            this.buttonArm.setVisibility(8);
            this.buttonDisarm.setVisibility(8);
        } else {
            this.buttonClear.setVisibility(8);
            if (this.PARTITION.isArmed()) {
                this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_lock_opened, null));
                this.buttonArm.setVisibility(8);
                this.buttonDisarm.setVisibility(0);
            } else {
                this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_lock_closed, null));
                this.buttonDisarm.setVisibility(8);
                this.buttonArm.setVisibility(0);
            }
        }
        ViewCompat.setElevation(this.imageButton, 10.0f);
        this.inputPasswd = (EditText) findViewById(R.id.input_passwd);
        this.inputPasswd.setOnEditorActionListener(new C19464());
        setTitle(getString(R.string.SWCPartitionActivity_TitleLabel) + " : " + this.PARTITION.getName());
        this.vto = this.frameLayout.getViewTreeObserver();
        this.vto.addOnGlobalLayoutListener(new C19475());
    }

    public void onDestroy() {
        Partition partition = this.PARTITION;
        Partition.ACTION_STATUS = 0;
        NexoTalk.removeNexoTalkListener(this);
        super.onDestroy();
    }

    public void onStatusUpdate(IElement el, boolean finish) {
    }

    public void onPartitionAlarm(IElement el) {
    }

    public void connectionStatus(boolean connected) {
    }

    public void onImportEnd(ArrayList<Integer> arrayList) {
    }

    public void onImport(int type, int iterator) {
    }

    public void connectionProcessInfo(String info, String error) {
    }
}
