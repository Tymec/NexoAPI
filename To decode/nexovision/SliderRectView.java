package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.search.SearchAuth.StatusCodes;
import eu.nexwell.android.nexovision.model.Dimmer;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Thermostat;
import eu.nexwell.android.nexovision.model.Ventilator;
import nexovision.android.nexwell.eu.nexovision.R;
import org.apache.http.HttpStatus;

public class SliderRectView extends FrameLayout {
    private int _touch_margin;
    private FrameLayout ecSlider;
    private ImageView ecSliderClipper;
    private AutoResizeTextView ecSliderText;

    public SliderRectView(Context context) {
        super(context);
        init(5);
    }

    public SliderRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(5);
    }

    public SliderRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(5);
    }

    public void init(int touch_margin) {
        inflate(getContext(), R.layout.slider_rect_view, this);
        this.ecSlider = (FrameLayout) findViewById(R.id.ecSlider);
        this.ecSliderClipper = (ImageView) findViewById(R.id.ecSliderClipper);
        this.ecSliderText = (AutoResizeTextView) this.ecSlider.findViewById(R.id.ecSliderText);
        this._touch_margin = touch_margin;
    }

    public float show(int top_margin, int height, int xpos, float value) {
        MarginLayoutParams params = (MarginLayoutParams) this.ecSlider.getLayoutParams();
        params.topMargin = top_margin;
        params.height = height;
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
        LayoutParams rparams = (LayoutParams) this.ecSliderText.getLayoutParams();
        if ((xrelpos * 100) / width > 50) {
            rparams.leftMargin = (this._touch_margin * this.ecSlider.getWidth()) / 100;
            this.ecSliderText.setGravity(19);
        } else {
            rparams.leftMargin = (((this._touch_margin * this.ecSlider.getWidth()) / 100) + width) - this.ecSliderText.getWidth();
            this.ecSliderText.setGravity(21);
        }
        int percent = (xrelpos * 100) / width;
        if (NVModel.CURR_ELEMENT instanceof Thermometer) {
            Thermostat t = ((Thermometer) NVModel.CURR_ELEMENT).getThermostat();
            if (t != null) {
                float min = t.getMin().floatValue();
                value = ((((float) xrelpos) * (t.getMax().floatValue() - min)) / ((float) width)) + min;
                this.ecSliderText.setText(String.format("%.1f℃", new Object[]{Float.valueOf(value)}));
                this.ecSliderClipper.setImageResource(R.drawable.slider_clip_temperature);
            }
        } else {
            this.ecSliderText.setText(((int) value) + "%");
            if (NVModel.CURR_ELEMENT instanceof Dimmer) {
                this.ecSliderClipper.setImageResource(R.drawable.slider_clip_light);
            } else if (NVModel.CURR_ELEMENT instanceof Ventilator) {
                this.ecSliderClipper.setImageResource(R.drawable.slider_clip_ventilator);
            } else {
                this.ecSliderClipper.setImageResource(R.drawable.slider_clip_analogout);
            }
        }
        this.ecSliderText.setLayoutParams(rparams);
        if (percent <= 0) {
            ((ClipDrawable) this.ecSliderClipper.getDrawable()).setLevel(0);
        } else if (percent >= 100) {
            ((ClipDrawable) this.ecSliderClipper.getDrawable()).setLevel(StatusCodes.AUTH_DISABLED);
        } else {
            ((ClipDrawable) this.ecSliderClipper.getDrawable()).setLevel(((percent * GamesStatusCodes.STATUS_VIDEO_NOT_ACTIVE) / 100) + HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        this.ecSlider.setVisibility(0);
        this.ecSlider.requestLayout();
        return value;
    }

    public float move(int xpos, float value) {
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
        final LayoutParams rparams = (LayoutParams) this.ecSliderText.getLayoutParams();
        if ((xrelpos * 100) / width > 50) {
            rparams.leftMargin = (this._touch_margin * this.ecSlider.getWidth()) / 100;
            this.ecSliderText.setGravity(19);
        } else {
            rparams.leftMargin = (((this._touch_margin * this.ecSlider.getWidth()) / 100) + width) - this.ecSliderText.getWidth();
            this.ecSliderText.setGravity(21);
        }
        int finalXrelpos = xrelpos;
        final int percent = (finalXrelpos * 100) / width;
        if (NVModel.CURR_ELEMENT instanceof Thermometer) {
            Thermostat t = ((Thermometer) NVModel.CURR_ELEMENT).getThermostat();
            if (t != null) {
                float min = t.getMin().floatValue();
                value = ((((float) finalXrelpos) * (t.getMax().floatValue() - min)) / ((float) width)) + min;
                final float finalValue1 = value;
                MainActivity.handler.post(new Runnable() {
                    public void run() {
                        SliderRectView.this.ecSliderText.setText(String.format("%.1f℃", new Object[]{Float.valueOf(finalValue1)}));
                    }
                });
            }
        } else {
            value = (float) percent;
            final float finalValue2 = value;
            MainActivity.handler.post(new Runnable() {
                public void run() {
                    SliderRectView.this.ecSliderText.setText(((int) finalValue2) + "%");
                }
            });
        }
        MainActivity.handler.post(new Runnable() {
            public void run() {
                SliderRectView.this.ecSliderText.setLayoutParams(rparams);
                if (percent <= 0) {
                    ((ClipDrawable) SliderRectView.this.ecSliderClipper.getDrawable()).setLevel(0);
                } else if (percent >= 100) {
                    ((ClipDrawable) SliderRectView.this.ecSliderClipper.getDrawable()).setLevel(StatusCodes.AUTH_DISABLED);
                } else {
                    ((ClipDrawable) SliderRectView.this.ecSliderClipper.getDrawable()).setLevel(((percent * GamesStatusCodes.STATUS_VIDEO_NOT_ACTIVE) / 100) + HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
                SliderRectView.this.ecSlider.requestLayout();
            }
        });
        return value;
    }

    public void hide() {
        this.ecSlider.setVisibility(4);
    }
}
