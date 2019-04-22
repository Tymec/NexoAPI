package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import org.bouncycastle.asn1.eac.EACTags;
import org.bouncycastle.crypto.tls.CipherSuite;

public class SliderView extends ImageView {
    private int COLOR_LOCAL = Color.rgb(200, 220, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    private int COLOR_REMOTE = Color.rgb(Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 240, 200);
    private int TEXT_COLOR_LOCAL = Color.rgb(100, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, 150);
    private int TEXT_COLOR_REMOTE = Color.rgb(150, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA, 100);
    private Rect bounds;
    private Rect clip_Rect = new Rect();
    private int[] gradient_color = new int[]{Color.parseColor("#000000"), Color.parseColor("#FFFFFF")};
    private int height = 20;
    private SliderListener listener;
    private Rect main_bar_Rect = new Rect();
    private int max = 100;
    private int min = 0;
    private Rect move_bar_Rect = new Rect();
    private boolean on = true;
    private boolean onoff_with_value = false;
    private final Path path = new Path();
    private float progress = 0.0f;
    private float progress_shutter = 0.0f;
    private boolean sliderIsPushed = false;
    private Bitmap slider_background;
    private Bitmap slider_background_push;
    private Paint slider_bar = new Paint();
    private Paint slider_bar2 = new Paint();
    private Paint slider_bar_bkg = new Paint();
    private Paint slider_bar_bkg2 = new Paint();
    private Paint slider_bar_text = new Paint();
    private Paint slider_bar_text2 = new Paint();
    private Paint slider_bitmaps = new Paint();
    private Bitmap slider_diode_off;
    private Bitmap slider_diode_off_push;
    private Bitmap slider_diode_on;
    private Bitmap slider_diode_on_push;
    private Bitmap slider_groove;
    private Paint slider_main_bar = new Paint();
    private Bitmap slider_pointer;
    private Bitmap slider_pointer_push;
    private int slider_style = 0;
    private Rect state_bar_Rect = new Rect();
    private String unit = "";
    private int width = 200;

    public interface SliderListener {
        void onSliderChanged(int i, boolean z);
    }

    /* renamed from: eu.nexwell.android.nexovision.SliderView$1 */
    class C20991 implements OnTouchListener {
        C20991() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            if (x >= SliderView.this.main_bar_Rect.left && x <= SliderView.this.main_bar_Rect.right) {
                SliderView.this.progress = (float) (x - SliderView.this.main_bar_Rect.left);
                SliderView.this.progress = (float) ((((int) SliderView.this.progress) * 252) / SliderView.this.main_bar_Rect.width());
                SliderView.this.notifyListener((int) SliderView.this.progress, true);
                SliderView.this.postInvalidate();
            } else if (x < SliderView.this.main_bar_Rect.left) {
                SliderView.this.progress = 0.0f;
                SliderView.this.notifyListener((int) SliderView.this.progress, true);
                SliderView.this.postInvalidate();
            } else if (x > SliderView.this.main_bar_Rect.right) {
                SliderView.this.progress = 252.0f;
                SliderView.this.notifyListener((int) SliderView.this.progress, true);
                SliderView.this.postInvalidate();
            }
            return true;
        }
    }

    public void setSliderListener(SliderListener l) {
        this.listener = l;
    }

    public SliderView(Context context) {
        super(context);
    }

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize(String unit, int slider_style, boolean onoff_with_value) {
        this.unit = unit;
        this.slider_style = slider_style;
        this.onoff_with_value = onoff_with_value;
        this.bounds = new Rect();
        this.slider_main_bar.setColor(Color.rgb(200, 220, Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
        this.slider_main_bar.setAntiAlias(true);
        this.slider_bar.setColor(this.COLOR_LOCAL);
        this.slider_bar.setAntiAlias(true);
        this.slider_bar2.setColor(this.COLOR_REMOTE);
        this.slider_bar2.setAntiAlias(true);
        this.slider_bar_bkg.setColor(Color.rgb(EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY));
        this.slider_bar_bkg.setAntiAlias(true);
        this.slider_bar_bkg2.setColor(Color.rgb(90, 90, 90));
        this.slider_bar_bkg2.setAntiAlias(true);
        this.slider_bar_text.setColor(this.TEXT_COLOR_LOCAL);
        this.slider_bar_text.setAntiAlias(true);
        this.slider_bar_text.setTextSize(12.0f);
        this.slider_bar_text2.setColor(this.TEXT_COLOR_REMOTE);
        this.slider_bar_text2.setAntiAlias(true);
        this.slider_bitmaps.setAntiAlias(true);
        this.slider_bitmaps.setFilterBitmap(true);
        this.progress_shutter = this.progress;
        setOnTouchListener(new C20991());
    }

    private void drawSlider(Canvas canvas) {
        this.slider_bar.setColor(this.COLOR_LOCAL);
        this.slider_bar2.setColor(this.COLOR_REMOTE);
        this.slider_bar_text.setColor(this.TEXT_COLOR_LOCAL);
        this.slider_bar_text2.setColor(this.TEXT_COLOR_REMOTE);
        drawBar3(canvas, 0, (int) this.progress_shutter, this.slider_bar2);
        drawBar(canvas, 0, (int) this.progress, this.slider_bar);
    }

    private void drawBar(Canvas canvas, int startPoint, int length, Paint paint) {
        if (length >= 0) {
            int len_relen = (this.main_bar_Rect.width() * ((length / 4) * 4)) / 252;
            this.path.reset();
            this.path.moveTo((float) (this.move_bar_Rect.left + len_relen), (float) (this.move_bar_Rect.bottom + ((this.main_bar_Rect.height() * 2) / 5)));
            this.path.lineTo((float) ((this.move_bar_Rect.left + len_relen) - (this.main_bar_Rect.height() / 3)), (float) this.move_bar_Rect.bottom);
            this.path.lineTo((float) ((this.move_bar_Rect.left + len_relen) + (this.main_bar_Rect.height() / 3)), (float) this.move_bar_Rect.bottom);
            this.path.close();
            canvas.drawPath(this.path, paint);
            this.path.reset();
            this.path.moveTo((float) (this.move_bar_Rect.left - (this.main_bar_Rect.height() / 3)), (float) this.move_bar_Rect.top);
            this.path.lineTo((float) ((this.move_bar_Rect.left + len_relen) + (this.main_bar_Rect.height() / 3)), (float) this.move_bar_Rect.top);
            this.path.close();
            canvas.drawPath(this.path, paint);
            canvas.drawRect((float) this.move_bar_Rect.left, (float) this.move_bar_Rect.top, (float) this.move_bar_Rect.right, (float) this.move_bar_Rect.bottom, paint);
        }
    }

    private void drawBar3(Canvas canvas, int startPoint, int value, Paint paint) {
        if (value >= 0) {
            int len = (this.main_bar_Rect.width() * value) / 252;
            this.path.reset();
            this.path.moveTo((float) (this.state_bar_Rect.left + len), (float) (this.state_bar_Rect.top - ((this.main_bar_Rect.height() * 2) / 5)));
            this.path.lineTo((float) ((this.state_bar_Rect.left + len) - (this.main_bar_Rect.height() / 3)), (float) this.state_bar_Rect.top);
            this.path.lineTo((float) ((this.state_bar_Rect.left + len) + (this.main_bar_Rect.height() / 3)), (float) this.state_bar_Rect.top);
            this.path.close();
            canvas.drawPath(this.path, paint);
            this.path.reset();
            this.path.moveTo((float) (this.state_bar_Rect.left - (this.main_bar_Rect.height() / 3)), (float) this.state_bar_Rect.bottom);
            this.path.lineTo((float) ((this.state_bar_Rect.left + len) + (this.main_bar_Rect.height() / 3)), (float) this.state_bar_Rect.bottom);
            this.path.close();
            canvas.drawPath(this.path, paint);
            canvas.drawRect((float) this.state_bar_Rect.left, (float) this.state_bar_Rect.top, (float) this.state_bar_Rect.right, (float) this.state_bar_Rect.bottom, paint);
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    private void notifyListener(int arg, boolean on) {
        if (this.listener != null) {
            this.listener.onSliderChanged(arg, on);
        }
    }

    public void setBorderValues(int Min, int Max) {
        this.min = Min;
        this.max = Max;
    }

    public void setValue(int Value, boolean move_slider) {
        this.progress_shutter = (float) Value;
        if (move_slider) {
            this.progress = this.progress_shutter;
        }
        postInvalidate();
    }

    public void setValue2(int Value, boolean move_shutter) {
        this.progress = (float) Value;
        if (move_shutter) {
            this.progress_shutter = this.progress;
        }
        postInvalidate();
    }

    public void setOn(boolean on) {
        this.on = on;
        postInvalidate();
    }

    public int getValue() {
        return (int) this.progress;
    }

    public void setGradientColor(int color1, int color2) {
        this.gradient_color[0] = color1;
        this.gradient_color[1] = color2;
        postInvalidate();
    }

    protected void onDraw(Canvas c) {
        c.getClipBounds(this.bounds);
        int padLeft = getPaddingLeft();
        int padTop = getPaddingTop();
        int padRight = getPaddingRight();
        int padBottom = getPaddingBottom();
        this.width = this.bounds.width() - (padLeft + padRight);
        this.height = this.bounds.height() - (padTop + padBottom);
        this.main_bar_Rect.left = this.bounds.left + padLeft;
        this.main_bar_Rect.top = (this.bounds.top + padTop) + (this.height / 16);
        this.main_bar_Rect.right = this.bounds.right - padRight;
        this.main_bar_Rect.bottom = (this.bounds.bottom - padBottom) - (this.height / 16);
        this.state_bar_Rect.left = this.bounds.left + padLeft;
        this.state_bar_Rect.top = this.main_bar_Rect.bottom;
        this.state_bar_Rect.right = this.bounds.right - padRight;
        this.state_bar_Rect.bottom = this.bounds.bottom - padBottom;
        this.move_bar_Rect.left = this.bounds.left + padLeft;
        this.move_bar_Rect.top = this.bounds.top + padTop;
        this.move_bar_Rect.right = this.bounds.right - padRight;
        this.move_bar_Rect.bottom = this.main_bar_Rect.top;
        this.slider_bar_text.setTextSize((float) (this.move_bar_Rect.height() * 2));
        this.slider_bar_text2.setTextSize((float) (this.state_bar_Rect.height() * 2));
        this.clip_Rect.left = this.bounds.left + padLeft;
        this.clip_Rect.top = this.bounds.top + padTop;
        this.clip_Rect.right = this.bounds.right - padRight;
        this.clip_Rect.bottom = this.bounds.bottom - padBottom;
        c.clipRect(this.clip_Rect);
        GradientDrawable gradient = new GradientDrawable(Orientation.LEFT_RIGHT, this.gradient_color);
        gradient.setGradientType(0);
        gradient.setDither(true);
        gradient.setGradientCenter((float) (this.bounds.left + (this.bounds.width() / 2)), (float) (this.bounds.top + (this.bounds.height() / 2)));
        gradient.setBounds(this.main_bar_Rect);
        gradient.draw(c);
        if (this.on) {
            this.COLOR_LOCAL = Color.rgb(200, 220, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            this.COLOR_REMOTE = Color.rgb(Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 240, 200);
            this.TEXT_COLOR_LOCAL = Color.rgb(100, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, 150);
            this.TEXT_COLOR_REMOTE = Color.rgb(150, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA, 100);
            this.slider_bar.setAlpha(255);
            this.slider_bar2.setAlpha(255);
        } else {
            this.COLOR_LOCAL = Color.rgb(128, 128, 128);
            this.COLOR_REMOTE = Color.rgb(160, 160, 160);
            this.TEXT_COLOR_LOCAL = Color.rgb(196, 196, 196);
            this.TEXT_COLOR_REMOTE = Color.rgb(236, 236, 236);
            this.slider_bar.setAlpha(32);
            this.slider_bar2.setAlpha(32);
        }
        drawSlider(c);
        super.onDraw(c);
    }
}
