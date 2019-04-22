package eu.nexwell.android.nexovision;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class ElementControl_PartitionActivity extends AppCompatActivity implements NexoTalkListener {
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

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$1 */
    class C19481 implements OnClickListener {
        C19481() {
        }

        public void onClick(View v) {
            if (ElementControl_PartitionActivity.this.inputPasswd.getText().toString() != null && !ElementControl_PartitionActivity.this.inputPasswd.getText().toString().isEmpty()) {
                NexoService.queueActionAndUpdate(ElementControl_PartitionActivity.this.PARTITION, ElementControl_PartitionActivity.this.PARTITION.clear(ElementControl_PartitionActivity.this.inputPasswd.getText().toString()));
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$2 */
    class C19492 implements OnClickListener {
        C19492() {
        }

        public void onClick(View v) {
            if (ElementControl_PartitionActivity.this.inputPasswd.getText().toString() != null && !ElementControl_PartitionActivity.this.inputPasswd.getText().toString().isEmpty()) {
                NexoService.queueActionAndUpdate(ElementControl_PartitionActivity.this.PARTITION, ElementControl_PartitionActivity.this.PARTITION.arm(ElementControl_PartitionActivity.this.inputPasswd.getText().toString()));
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$3 */
    class C19503 implements OnClickListener {
        C19503() {
        }

        public void onClick(View v) {
            if (ElementControl_PartitionActivity.this.inputPasswd.getText().toString() != null && !ElementControl_PartitionActivity.this.inputPasswd.getText().toString().isEmpty()) {
                NexoService.queueActionAndUpdate(ElementControl_PartitionActivity.this.PARTITION, ElementControl_PartitionActivity.this.PARTITION.disarm(ElementControl_PartitionActivity.this.inputPasswd.getText().toString()));
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$4 */
    class C19514 implements OnEditorActionListener {
        C19514() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && event.getKeyCode() == 66) || actionId == 6) {
                if (ElementControl_PartitionActivity.this.buttonArm.getVisibility() == 0) {
                    ElementControl_PartitionActivity.this.buttonArm.performClick();
                }
                if (ElementControl_PartitionActivity.this.buttonDisarm.getVisibility() == 0) {
                    ElementControl_PartitionActivity.this.buttonDisarm.performClick();
                }
                if (ElementControl_PartitionActivity.this.buttonClear.getVisibility() == 0) {
                    ElementControl_PartitionActivity.this.buttonClear.performClick();
                }
            }
            return false;
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$5 */
    class C19525 implements OnGlobalLayoutListener {
        C19525() {
        }

        public void onGlobalLayout() {
            int width = 10;
            if (ElementControl_PartitionActivity.this.buttonClear.getVisibility() == 0) {
                width = ElementControl_PartitionActivity.this.buttonClear.getMeasuredWidth();
                ElementControl_PartitionActivity.this.buttonClear.setTextSize(0, (float) (width / 10));
                ElementControl_PartitionActivity.this.buttonClear.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_PartitionActivity.this.buttonDisarm.getVisibility() == 0) {
                width = ElementControl_PartitionActivity.this.buttonDisarm.getMeasuredWidth();
                ElementControl_PartitionActivity.this.buttonDisarm.setTextSize(0, (float) (width / 10));
                ElementControl_PartitionActivity.this.buttonDisarm.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_PartitionActivity.this.buttonArm.getVisibility() == 0) {
                width = ElementControl_PartitionActivity.this.buttonArm.getMeasuredWidth();
                ElementControl_PartitionActivity.this.buttonArm.setTextSize(0, (float) (width / 10));
                ElementControl_PartitionActivity.this.buttonArm.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_PartitionActivity.this.buttonOk.getVisibility() == 0) {
                width = ElementControl_PartitionActivity.this.buttonOk.getMeasuredWidth();
                ElementControl_PartitionActivity.this.buttonOk.setTextSize(0, (float) (width / 10));
                ElementControl_PartitionActivity.this.buttonOk.setPadding(0, 0, 0, width / 8);
            } else if (ElementControl_PartitionActivity.this.buttonFail.getVisibility() == 0) {
                width = ElementControl_PartitionActivity.this.buttonFail.getMeasuredWidth();
                ElementControl_PartitionActivity.this.buttonFail.setTextSize(0, (float) (width / 10));
                ElementControl_PartitionActivity.this.buttonFail.setPadding(0, 0, 0, width / 8);
            }
            LayoutParams lp = (LayoutParams) ElementControl_PartitionActivity.this.imageButton.getLayoutParams();
            lp.width = (width / 3) * 2;
            lp.height = lp.width;
            lp.setMargins(0, -(width / 24), 0, width / 24);
            ElementControl_PartitionActivity.this.imageButton.setLayoutParams(lp);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$6 */
    class C19536 implements Runnable {
        C19536() {
        }

        public void run() {
            ElementControl_PartitionActivity.this.buttonClear.setVisibility(8);
            ElementControl_PartitionActivity.this.buttonArm.setVisibility(8);
            ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(8);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$7 */
    class C19557 implements Runnable {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$7$1 */
        class C19541 implements Runnable {
            C19541() {
            }

            public void run() {
                ElementControl_PartitionActivity.this.finish();
            }
        }

        C19557() {
        }

        public void run() {
            ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_thumb_up, null));
            ElementControl_PartitionActivity.this.buttonOk.setVisibility(0);
            ElementControl_PartitionActivity.this.buttonFail.setVisibility(8);
            ElementControl_PartitionActivity.handler.postDelayed(new C19541(), 1000);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$8 */
    class C19568 implements Runnable {
        C19568() {
        }

        public void run() {
            ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_warning, null));
            ElementControl_PartitionActivity.this.buttonFail.setVisibility(0);
            ElementControl_PartitionActivity.this.buttonOk.setVisibility(8);
            ElementControl_PartitionActivity.this.inputPasswd.setText("");
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$9 */
    class C19619 implements Runnable {

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$9$1 */
        class C19571 implements Runnable {
            C19571() {
            }

            public void run() {
                ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_ring, null));
                ElementControl_PartitionActivity.this.buttonClear.setVisibility(0);
                ElementControl_PartitionActivity.this.buttonArm.setVisibility(8);
                ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(8);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$9$2 */
        class C19582 implements Runnable {
            C19582() {
            }

            public void run() {
                ElementControl_PartitionActivity.this.buttonClear.setVisibility(8);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$9$3 */
        class C19593 implements Runnable {
            C19593() {
            }

            public void run() {
                ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_lock_opened, null));
                ElementControl_PartitionActivity.this.buttonArm.setVisibility(8);
                ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(0);
            }
        }

        /* renamed from: eu.nexwell.android.nexovision.ElementControl_PartitionActivity$9$4 */
        class C19604 implements Runnable {
            C19604() {
            }

            public void run() {
                ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_lock_closed, null));
                ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(8);
                ElementControl_PartitionActivity.this.buttonArm.setVisibility(0);
            }
        }

        C19619() {
        }

        public void run() {
            if (ElementControl_PartitionActivity.this.PARTITION.isAlarming()) {
                ElementControl_PartitionActivity.handler.post(new C19571());
            } else {
                ElementControl_PartitionActivity.handler.post(new C19582());
                if (ElementControl_PartitionActivity.this.PARTITION.isArmed()) {
                    ElementControl_PartitionActivity.handler.post(new C19593());
                } else {
                    ElementControl_PartitionActivity.handler.post(new C19604());
                }
            }
            ElementControl_PartitionActivity.this.buttonFail.setVisibility(8);
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
        this.buttonClear.setOnClickListener(new C19481());
        this.buttonArm = (SquareButton) findViewById(R.id.buttonArm);
        this.buttonArm.setOnClickListener(new C19492());
        this.buttonDisarm = (SquareButton) findViewById(R.id.buttonDisarm);
        this.buttonDisarm.setOnClickListener(new C19503());
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
        this.inputPasswd.setOnEditorActionListener(new C19514());
        setTitle(getString(R.string.SWCPartitionActivity_TitleLabel) + " : " + this.PARTITION.getName());
        this.vto = this.frameLayout.getViewTreeObserver();
        this.vto.addOnGlobalLayoutListener(new C19525());
    }

    protected void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        Partition partition = this.PARTITION;
        Partition.ACTION_STATUS = 0;
        NexoTalk.removeNexoTalkListener(this);
        super.onDestroy();
    }

    public void onStatusUpdate(IElement el, boolean finish) {
        if (el != null && (el instanceof Partition) && el == this.PARTITION) {
            StringBuilder append = new StringBuilder().append("onStatusUpdate:ACTION_STATUS=");
            Partition partition = this.PARTITION;
            Log.d("EC_PartitionActivity", append.append(Partition.ACTION_STATUS).toString());
            Partition partition2 = this.PARTITION;
            if (Partition.ACTION_STATUS > 0) {
                handler.post(new C19536());
                partition2 = this.PARTITION;
                if (Partition.ACTION_STATUS == 1) {
                    handler.post(new C19557());
                } else {
                    handler.post(new C19568());
                    handler.postDelayed(new C19619(), 1000);
                }
            } else if (this.PARTITION.isAlarming()) {
                handler.post(new Runnable() {
                    public void run() {
                        ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_ring, null));
                        ElementControl_PartitionActivity.this.buttonClear.setVisibility(0);
                        ElementControl_PartitionActivity.this.buttonArm.setVisibility(8);
                        ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(8);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    public void run() {
                        ElementControl_PartitionActivity.this.buttonClear.setVisibility(8);
                    }
                });
                if (this.PARTITION.isArmed()) {
                    handler.post(new Runnable() {
                        public void run() {
                            ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_lock_opened, null));
                            ElementControl_PartitionActivity.this.buttonArm.setVisibility(8);
                            ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(0);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            ElementControl_PartitionActivity.this.imageButton.setImageDrawable(ResourcesCompat.getDrawable(ElementControl_PartitionActivity.this.getResources(), R.drawable.ic_lock_closed, null));
                            ElementControl_PartitionActivity.this.buttonDisarm.setVisibility(8);
                            ElementControl_PartitionActivity.this.buttonArm.setVisibility(0);
                        }
                    });
                }
            }
            handler.post(new Runnable() {
                public void run() {
                    ElementControl_PartitionActivity.this.frameLayout.requestLayout();
                }
            });
        }
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
