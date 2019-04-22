package eu.nexwell.android.nexovision;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import eu.nexwell.android.nexovision.ColorBoxView.ColorBoxListener;
import eu.nexwell.android.nexovision.SliderView.SliderListener;
import eu.nexwell.android.nexovision.communication.NexoService;
import eu.nexwell.android.nexovision.communication.NexoTalk;
import eu.nexwell.android.nexovision.communication.NexoTalkListener;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.RGBW;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class ElementControl_RGBWActivity extends AppCompatActivity implements NexoTalkListener {
    private static String LOG_TAG = "";
    public static SeekBar SCRGBWSeekBar_Blue;
    public static SeekBar SCRGBWSeekBar_Green;
    public static SeekBar SCRGBWSeekBar_Red;
    /* renamed from: b */
    public static int f25b = 0;
    private static Context context;
    public static int curr_state = 3;
    /* renamed from: g */
    public static int f26g = 0;
    private static Handler handler;
    /* renamed from: r */
    public static int f27r = 0;
    public static int rb = 0;
    public static int rg = 0;
    public static int rr = 0;
    private int CURR_COL = ViewCompat.MEASURED_STATE_MASK;
    private ColorBoxView ColorBoxViewLR;
    private boolean ONCE_DONE = false;
    private Button colorButton1;
    private Button colorButton2;
    private Button colorButton3;
    private Button colorButton4;
    private Button colorButton5;
    private Button colorButton6;
    private Button colorButton7;
    private Button colorButton8;
    private SliderView jogView1;
    private SliderView jogView2;
    private SliderView jogView3;

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$1 */
    class C19621 implements ColorBoxListener {
        C19621() {
        }

        public void onColorBoxChanged(int arg) {
        }

        public void onColorBoxRequest() {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$2 */
    class C19632 implements OnClickListener {
        C19632() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(SupportMenu.CATEGORY_MASK);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$3 */
    class C19643 implements OnClickListener {
        C19643() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(Color.parseColor("#FFFF7F00"));
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$4 */
    class C19654 implements OnClickListener {
        C19654() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(InputDeviceCompat.SOURCE_ANY);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$5 */
    class C19665 implements OnClickListener {
        C19665() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(-16711936);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$6 */
    class C19676 implements OnClickListener {
        C19676() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(Color.parseColor("#FF00FFFF"));
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$7 */
    class C19687 implements OnClickListener {
        C19687() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(-16776961);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$8 */
    class C19698 implements OnClickListener {
        C19698() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(Color.parseColor("#FFFF00FF"));
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ElementControl_RGBWActivity$9 */
    class C19709 implements OnClickListener {
        C19709() {
        }

        public void onClick(View v) {
            ElementControl_RGBWActivity.this.applyColor(-1);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        LOG_TAG = getClass().getName();
        setContentView(R.layout.activity_elementcontrol_rgbw);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (!(NVModel.CURR_ELEMENT instanceof RGBW)) {
            finish();
        }
        NexoTalk.addNexoTalkListener(this);
        this.ColorBoxViewLR = (ColorBoxView) findViewById(R.id.ColorBoxViewLR);
        this.ColorBoxViewLR.setStyle(1);
        this.ColorBoxViewLR.initialize();
        this.ColorBoxViewLR.setValue(this.CURR_COL);
        this.ColorBoxViewLR.setColorBoxListener(new C19621());
        this.colorButton1 = (Button) findViewById(R.id.color_button_1);
        this.colorButton1.setBackgroundColor(SupportMenu.CATEGORY_MASK);
        this.colorButton1.setOnClickListener(new C19632());
        this.colorButton2 = (Button) findViewById(R.id.color_button_2);
        this.colorButton2.setBackgroundColor(Color.parseColor("#FFFF7F00"));
        this.colorButton2.setOnClickListener(new C19643());
        this.colorButton3 = (Button) findViewById(R.id.color_button_3);
        this.colorButton3.setBackgroundColor(InputDeviceCompat.SOURCE_ANY);
        this.colorButton3.setOnClickListener(new C19654());
        this.colorButton4 = (Button) findViewById(R.id.color_button_4);
        this.colorButton4.setBackgroundColor(-16711936);
        this.colorButton4.setOnClickListener(new C19665());
        this.colorButton5 = (Button) findViewById(R.id.color_button_5);
        this.colorButton5.setBackgroundColor(Color.parseColor("#FF00FFFF"));
        this.colorButton5.setOnClickListener(new C19676());
        this.colorButton6 = (Button) findViewById(R.id.color_button_6);
        this.colorButton6.setBackgroundColor(-16776961);
        this.colorButton6.setOnClickListener(new C19687());
        this.colorButton7 = (Button) findViewById(R.id.color_button_7);
        this.colorButton7.setBackgroundColor(Color.parseColor("#FFFF00FF"));
        this.colorButton7.setOnClickListener(new C19698());
        this.colorButton8 = (Button) findViewById(R.id.color_button_8);
        this.colorButton8.setBackgroundColor(-1);
        this.colorButton8.setOnClickListener(new C19709());
        this.jogView1 = (SliderView) findViewById(R.id.jogView1);
        this.jogView1.initialize("", 0, false);
        this.jogView1.setBorderValues(0, 255);
        this.jogView1.setGradientColor(this.CURR_COL, Color.rgb(255, Color.green(this.CURR_COL), Color.blue(this.CURR_COL)));
        this.jogView1.setSliderListener(new SliderListener() {
            public void onSliderChanged(int arg, boolean on) {
                ElementControl_RGBWActivity.f27r = arg;
                ElementControl_RGBWActivity.this.CURR_COL = Color.rgb(arg, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL));
                ElementControl_RGBWActivity.this.jogView2.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 0, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 255, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                ElementControl_RGBWActivity.this.jogView3.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 0), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 255));
                ElementControl_RGBWActivity.this.ColorBoxViewLR.setValue(ElementControl_RGBWActivity.this.CURR_COL);
            }
        });
        this.jogView2 = (SliderView) findViewById(R.id.jogView2);
        this.jogView2.initialize("", 0, false);
        this.jogView2.setBorderValues(0, 255);
        this.jogView2.setGradientColor(this.CURR_COL, Color.rgb(Color.red(this.CURR_COL), 255, Color.blue(this.CURR_COL)));
        this.jogView2.setSliderListener(new SliderListener() {
            public void onSliderChanged(int arg, boolean on) {
                ElementControl_RGBWActivity.f26g = arg;
                ElementControl_RGBWActivity.this.CURR_COL = Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), arg, Color.blue(ElementControl_RGBWActivity.this.CURR_COL));
                ElementControl_RGBWActivity.this.jogView1.setGradientColor(Color.rgb(0, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(255, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                ElementControl_RGBWActivity.this.jogView3.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 0), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 255));
                ElementControl_RGBWActivity.this.ColorBoxViewLR.setValue(ElementControl_RGBWActivity.this.CURR_COL);
            }
        });
        this.jogView3 = (SliderView) findViewById(R.id.jogView3);
        this.jogView3.initialize("", 0, false);
        this.jogView3.setBorderValues(0, 255);
        this.jogView3.setGradientColor(this.CURR_COL, Color.rgb(Color.red(this.CURR_COL), Color.green(this.CURR_COL), 255));
        this.jogView3.setSliderListener(new SliderListener() {
            public void onSliderChanged(int arg, boolean on) {
                ElementControl_RGBWActivity.f25b = arg;
                ElementControl_RGBWActivity.this.CURR_COL = Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), arg);
                ElementControl_RGBWActivity.this.jogView1.setGradientColor(Color.rgb(0, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(255, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                ElementControl_RGBWActivity.this.jogView2.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 0, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 255, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                ElementControl_RGBWActivity.this.ColorBoxViewLR.setValue(ElementControl_RGBWActivity.this.CURR_COL);
            }
        });
        ((ImageButton) findViewById(R.id.SCRGBWButton_ON)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NexoService.queueActionAndUpdate((ISwitch) NVModel.CURR_ELEMENT, ((RGBW) NVModel.CURR_ELEMENT).on());
            }
        });
        ((ImageButton) findViewById(R.id.SCRGBWButton_OFF)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NexoService.queueActionAndUpdate((ISwitch) NVModel.CURR_ELEMENT, ((RGBW) NVModel.CURR_ELEMENT).off());
            }
        });
        ((Button) findViewById(R.id.SCRGBWButton_SET)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NexoService.queueActionAndUpdate((ISwitch) NVModel.CURR_ELEMENT, ((RGBW) NVModel.CURR_ELEMENT).set(ElementControl_RGBWActivity.f27r, ElementControl_RGBWActivity.f26g, ElementControl_RGBWActivity.f25b));
            }
        });
        setTitle(getString(R.string.SWCRGBWActivity_TitleLabel) + " : " + NVModel.CURR_ELEMENT.getName());
    }

    private void applyColor(int col) {
        f27r = Color.red(col);
        f26g = Color.green(col);
        f25b = Color.blue(col);
        this.CURR_COL = col;
        this.jogView1.setValue2(f27r, false);
        this.jogView2.setValue2(f26g, false);
        this.jogView3.setValue2(f25b, false);
        this.jogView1.setGradientColor(Color.rgb(0, Color.green(this.CURR_COL), Color.blue(this.CURR_COL)), Color.rgb(255, Color.green(this.CURR_COL), Color.blue(this.CURR_COL)));
        this.jogView2.setGradientColor(Color.rgb(Color.red(this.CURR_COL), 0, Color.blue(this.CURR_COL)), Color.rgb(Color.red(this.CURR_COL), 255, Color.blue(this.CURR_COL)));
        this.jogView3.setGradientColor(Color.rgb(Color.red(this.CURR_COL), Color.green(this.CURR_COL), 0), Color.rgb(Color.red(this.CURR_COL), Color.green(this.CURR_COL), 255));
        this.ColorBoxViewLR.setValue(this.CURR_COL);
    }

    public void changeTitleLabelProjectName(String projectName) {
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.APP_NAME));
        actionBar.setSubtitle(projectName);
    }

    protected void onResume() {
        super.onResume();
        onStatusUpdate(NVModel.CURR_ELEMENT, true);
    }

    public void onDestroy() {
        NexoTalk.removeNexoTalkListener(this);
        super.onDestroy();
    }

    public void onStatusUpdate(IElement el, boolean finish) {
        if (el != null && el == NVModel.CURR_ELEMENT) {
            final boolean on = ((RGBW) el).isOn();
            final Integer r = ((RGBW) el).getValue()[0];
            final Integer g = ((RGBW) el).getValue()[1];
            final Integer b = ((RGBW) el).getValue()[2];
            handler.post(new Runnable() {
                public void run() {
                    if (ElementControl_RGBWActivity.this.ONCE_DONE) {
                        ElementControl_RGBWActivity.this.jogView1.setValue(r.intValue(), false);
                        ElementControl_RGBWActivity.this.jogView2.setValue(g.intValue(), false);
                        ElementControl_RGBWActivity.this.jogView3.setValue(b.intValue(), false);
                        if (on) {
                            ElementControl_RGBWActivity.this.ColorBoxViewLR.setOn(true);
                            ElementControl_RGBWActivity.this.jogView1.setOn(true);
                            ElementControl_RGBWActivity.this.jogView2.setOn(true);
                            ElementControl_RGBWActivity.this.jogView3.setOn(true);
                        } else {
                            ElementControl_RGBWActivity.this.ColorBoxViewLR.setOn(false);
                            ElementControl_RGBWActivity.this.jogView1.setOn(false);
                            ElementControl_RGBWActivity.this.jogView2.setOn(false);
                            ElementControl_RGBWActivity.this.jogView3.setOn(false);
                        }
                        ElementControl_RGBWActivity.this.ColorBoxViewLR.setValue2(Color.rgb(r.intValue(), g.intValue(), b.intValue()));
                        return;
                    }
                    ElementControl_RGBWActivity.this.jogView1.setValue(r.intValue(), true);
                    ElementControl_RGBWActivity.this.jogView2.setValue(g.intValue(), true);
                    ElementControl_RGBWActivity.this.jogView3.setValue(b.intValue(), true);
                    ElementControl_RGBWActivity.this.ONCE_DONE = !ElementControl_RGBWActivity.this.ONCE_DONE;
                    ElementControl_RGBWActivity.this.CURR_COL = Color.rgb(r.intValue(), Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL));
                    ElementControl_RGBWActivity.this.jogView2.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 0, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 255, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                    ElementControl_RGBWActivity.this.jogView3.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 0), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 255));
                    ElementControl_RGBWActivity.this.CURR_COL = Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), g.intValue(), Color.blue(ElementControl_RGBWActivity.this.CURR_COL));
                    ElementControl_RGBWActivity.this.jogView1.setGradientColor(Color.rgb(0, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(255, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                    ElementControl_RGBWActivity.this.jogView3.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 0), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), 255));
                    ElementControl_RGBWActivity.this.CURR_COL = Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), Color.green(ElementControl_RGBWActivity.this.CURR_COL), b.intValue());
                    ElementControl_RGBWActivity.this.jogView1.setGradientColor(Color.rgb(0, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(255, Color.green(ElementControl_RGBWActivity.this.CURR_COL), Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                    ElementControl_RGBWActivity.this.jogView2.setGradientColor(Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 0, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)), Color.rgb(Color.red(ElementControl_RGBWActivity.this.CURR_COL), 255, Color.blue(ElementControl_RGBWActivity.this.CURR_COL)));
                    if (on) {
                        ElementControl_RGBWActivity.this.ColorBoxViewLR.setOn(true);
                        ElementControl_RGBWActivity.this.jogView1.setOn(true);
                        ElementControl_RGBWActivity.this.jogView2.setOn(true);
                        ElementControl_RGBWActivity.this.jogView3.setOn(true);
                    } else {
                        ElementControl_RGBWActivity.this.ColorBoxViewLR.setOn(false);
                        ElementControl_RGBWActivity.this.jogView1.setOn(false);
                        ElementControl_RGBWActivity.this.jogView2.setOn(false);
                        ElementControl_RGBWActivity.this.jogView3.setOn(false);
                    }
                    ElementControl_RGBWActivity.this.ColorBoxViewLR.setValue(Color.rgb(r.intValue(), g.intValue(), b.intValue()));
                    ElementControl_RGBWActivity.this.ColorBoxViewLR.setValue2(Color.rgb(r.intValue(), g.intValue(), b.intValue()));
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
