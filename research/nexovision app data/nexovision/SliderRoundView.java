package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import eu.nexwell.android.nexovision.model.Dimmer;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Thermostat;
import eu.nexwell.android.nexovision.model.Ventilator;
import nexovision.android.nexwell.eu.nexovision.R;

public class SliderRoundView extends FrameLayout {
    private Point _center;
    private int _touch_margin;
    private FrameLayout ecSlider;
    private ECRoundSliderClipper ecSliderClipper;
    private AutoResizeTextView ecSliderText;

    public SliderRoundView(Context context) {
        super(context);
        init(5);
    }

    public SliderRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(5);
    }

    public SliderRoundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(5);
    }

    public void init(int touch_margin) {
        inflate(getContext(), R.layout.slider_round_view, this);
        this.ecSlider = (FrameLayout) findViewById(R.id.ecSlider);
        this.ecSliderClipper = (ECRoundSliderClipper) findViewById(R.id.ecSliderClipper);
        this.ecSliderText = (AutoResizeTextView) this.ecSlider.findViewById(R.id.ecSliderText);
        this._touch_margin = touch_margin;
        this._center = new Point(0, 0);
    }

    public float show(int top_margin, int left_margin, int height, int xpos, Point center, float value) {
        MarginLayoutParams params = (MarginLayoutParams) this.ecSlider.getLayoutParams();
        params.topMargin = top_margin;
        params.leftMargin = left_margin;
        params.height = height;
        params.width = height;
        this._center = center;
        this.ecSlider.setLayoutParams(params);
        int[] location = new int[2];
        this.ecSlider.getLocationOnScreen(location);
        int xrelpos = (xpos - location[0]) - ((this.ecSlider.getWidth() * (this._touch_margin - 1)) / 100);
        int width = (this.ecSlider.getWidth() * (100 - (this._touch_margin * 2))) / 100;
        if (xrelpos < 0) {
            xrelpos = 0;
        }
        if (xrelpos > width) {
            xrelpos = width;
        }
        int percent = (xrelpos * 100) / width;
        if (NVModel.CURR_ELEMENT instanceof Thermometer) {
            Thermostat t = ((Thermometer) NVModel.CURR_ELEMENT).getThermostat();
            if (t != null) {
                float min = t.getMin().floatValue();
                value = ((((float) xrelpos) * (t.getMax().floatValue() - min)) / ((float) width)) + min;
                this.ecSliderText.setText(String.format("%.1f℃", new Object[]{Float.valueOf(value)}));
            }
        } else {
            this.ecSliderText.setText(((int) value) + "%");
            if (!(NVModel.CURR_ELEMENT instanceof Dimmer) && (NVModel.CURR_ELEMENT instanceof Ventilator)) {
            }
        }
        this.ecSlider.setVisibility(0);
        this.ecSlider.requestLayout();
        return value;
    }

    public float move(final int xpos, final int ypos, float value) {
        this.ecSlider.getLocationOnScreen(new int[2]);
        final int angle = (int) calcRotationAngleInDegrees(this._center, new Point(xpos, ypos));
        if (NVModel.CURR_ELEMENT instanceof Thermometer) {
            Thermostat t = ((Thermometer) NVModel.CURR_ELEMENT).getThermostat();
            if (t != null) {
                float min = t.getMin().floatValue();
                value = ((((float) angle) * (t.getMax().floatValue() - min)) / 270.0f) + min;
                final float finalValue1 = value;
                MainActivity.handler.post(new Runnable() {
                    public void run() {
                        SliderRoundView.this.ecSliderText.setText(String.format("%.1f℃", new Object[]{Float.valueOf(finalValue1)}));
                    }
                });
            }
        } else {
            value = (float) ((angle * 100) / 270);
            final float finalValue2 = value;
            MainActivity.handler.post(new Runnable() {
                public void run() {
                    SliderRoundView.this.ecSliderText.setText(((int) finalValue2) + "%");
                }
            });
        }
        MainActivity.handler.post(new Runnable() {
            public void run() {
                Log.d("SliderRoundView", "Xpos=" + xpos + ",Ypos=" + ypos + ",Cx=" + SliderRoundView.this._center.x + ",Cy=" + SliderRoundView.this._center.y);
                SliderRoundView.this.ecSliderClipper.setAngle(angle);
                SliderRoundView.this.ecSlider.requestLayout();
            }
        });
        return value;
    }

    public void hide() {
        this.ecSlider.setVisibility(4);
    }

    public static double calcRotationAngleInDegrees(Point centerPt, Point targetPt) {
        double angle = Math.toDegrees(Math.atan2((double) (targetPt.y - centerPt.y), (double) (targetPt.x - centerPt.x)) + 1.5707963267948966d) - 225.0d;
        if (angle < -45.0d) {
            angle += 360.0d;
        } else if (angle < FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            angle = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        if (angle > 270.0d) {
            return 270.0d;
        }
        return angle;
    }
}
