package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import nexovision.android.nexwell.eu.nexovision.R;
import org.bouncycastle.asn1.eac.EACTags;

public class RotaryKnobView extends ImageView {
    private boolean ON = true;
    private float angle = 0.0f;
    private float angle_shutter = 0.0f;
    private Rect bounds;
    private boolean drawLocalValue = false;
    private ColorMatrixColorFilter greyScaleFilter;
    int height = 240;
    private boolean indicator = true;
    private boolean knobIsPushed = false;
    private Paint knob_arc = new Paint();
    private Paint knob_arc_bkg = new Paint();
    private Paint knob_arc_bkg2 = new Paint();
    private Paint knob_arc_text = new Paint();
    private Paint knob_arc_text2 = new Paint();
    private Bitmap knob_background;
    private Bitmap knob_background_push;
    private Paint knob_bitmaps = new Paint();
    private Bitmap knob_diode_off;
    private Bitmap knob_diode_off_push;
    private Bitmap knob_diode_on;
    private Bitmap knob_diode_on_push;
    private Bitmap knob_groove;
    private Bitmap knob_pointer;
    private Bitmap knob_pointer_push;
    private int knob_style = 0;
    private RotaryKnobListener listener;
    private int localBkgColor = Color.argb(127, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY);
    private int localValueColor = Color.argb(255, 200, 220, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    private int local_value;
    int margin = ((this.width / this.normal_divider) / 2);
    private int max = 100;
    private int min = 0;
    int normal_divider = 1;
    private boolean on = true;
    private boolean onoff_with_value = false;
    private final Path path = new Path();
    int pushed_divider = 1;
    private int reangled = 0;
    private int reangled_shutter = 0;
    private int targetBkgColor = Color.argb(127, 90, 90, 90);
    private int targetValueColor = Color.argb(255, Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 240, 200);
    private String unit = "";
    int width = 240;

    public interface RotaryKnobListener {
        void onKnobChanged(int i, boolean z);
    }

    public void setKnobListener(RotaryKnobListener l) {
        this.listener = l;
    }

    public RotaryKnobView(Context context) {
        super(context);
    }

    public RotaryKnobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C2072R.styleable.RotaryKnobView);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    this.localBkgColor = a.getColor(attr, this.localBkgColor);
                    break;
                case 1:
                    this.localValueColor = a.getColor(attr, this.localValueColor);
                    break;
                case 2:
                    this.targetBkgColor = a.getColor(attr, this.targetBkgColor);
                    break;
                case 3:
                    this.targetValueColor = a.getColor(attr, this.targetValueColor);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
    }

    public RotaryKnobView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize(String unit, int knob_style, final boolean onoff_with_value) {
        this.unit = unit;
        this.knob_style = knob_style;
        this.onoff_with_value = onoff_with_value;
        this.reangled = 0;
        this.angle = (float) (this.reangled + 135);
        this.reangled_shutter = this.reangled;
        this.angle_shutter = this.angle;
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                float length = PointF.length((float) (x - (RotaryKnobView.this.width / 2)), (float) (y - (RotaryKnobView.this.height / 2)));
                int tmpreangled;
                int tmpangle;
                if (length > ((float) (RotaryKnobView.this.width / 8)) && length < ((float) (RotaryKnobView.this.width / 2))) {
                    float tmp_angle = (float) RotaryKnobView.this.pointToAngle(x, y);
                    RotaryKnobView.this.reangled = ((int) tmp_angle) - 135;
                    if (RotaryKnobView.this.reangled < 0) {
                        RotaryKnobView.this.reangled = RotaryKnobView.this.reangled + 360;
                    }
                    if (RotaryKnobView.this.reangled >= 0 && RotaryKnobView.this.reangled <= 270) {
                        RotaryKnobView.this.angle = tmp_angle;
                        if (onoff_with_value) {
                            RotaryKnobView.this.ON = true;
                        }
                    } else if (RotaryKnobView.this.reangled <= 270 || RotaryKnobView.this.reangled >= 315) {
                        RotaryKnobView.this.reangled = 0;
                        RotaryKnobView.this.angle = (float) (RotaryKnobView.this.reangled + 135);
                        if (onoff_with_value) {
                            RotaryKnobView.this.ON = false;
                        }
                    } else {
                        RotaryKnobView.this.reangled = 270;
                        RotaryKnobView.this.angle = (float) (RotaryKnobView.this.reangled + 135);
                        if (onoff_with_value) {
                            RotaryKnobView.this.ON = true;
                        }
                    }
                    if (action == 1) {
                        RotaryKnobView.this.knobIsPushed = false;
                        RotaryKnobView.this.local_value = (int) Math.round((((double) (RotaryKnobView.this.reangled * (RotaryKnobView.this.max - RotaryKnobView.this.min))) / 270.0d) + ((double) RotaryKnobView.this.min));
                        RotaryKnobView.this.notifyListener(RotaryKnobView.this.local_value, RotaryKnobView.this.ON);
                        tmpreangled = ((RotaryKnobView.this.local_value - RotaryKnobView.this.min) * 270) / (RotaryKnobView.this.max - RotaryKnobView.this.min);
                        tmpangle = tmpreangled + 135;
                        RotaryKnobView.this.reangled = tmpreangled;
                        RotaryKnobView.this.angle = (float) tmpangle;
                    } else if (action == 0) {
                        RotaryKnobView.this.drawLocalValue = true;
                    }
                    RotaryKnobView.this.postInvalidate();
                } else if (length <= ((float) (RotaryKnobView.this.width / 8))) {
                    if (action == 0) {
                        RotaryKnobView.this.knobIsPushed = true;
                        RotaryKnobView.this.postInvalidate();
                    } else if (action == 1 && RotaryKnobView.this.knobIsPushed) {
                        if (!onoff_with_value) {
                            RotaryKnobView.this.ON = !RotaryKnobView.this.ON;
                        } else if (RotaryKnobView.this.reangled > 135) {
                            RotaryKnobView.this.reangled = 0;
                            RotaryKnobView.this.angle = (float) (RotaryKnobView.this.reangled + 135);
                            RotaryKnobView.this.ON = false;
                        } else {
                            RotaryKnobView.this.reangled = 270;
                            RotaryKnobView.this.angle = (float) (RotaryKnobView.this.reangled + 135);
                            RotaryKnobView.this.ON = true;
                        }
                        RotaryKnobView.this.knobIsPushed = false;
                        RotaryKnobView.this.local_value = (int) Math.round((((double) (RotaryKnobView.this.reangled * (RotaryKnobView.this.max - RotaryKnobView.this.min))) / 270.0d) + ((double) RotaryKnobView.this.min));
                        RotaryKnobView.this.notifyListener(RotaryKnobView.this.local_value, RotaryKnobView.this.ON);
                        tmpreangled = ((RotaryKnobView.this.local_value - RotaryKnobView.this.min) * 270) / (RotaryKnobView.this.max - RotaryKnobView.this.min);
                        tmpangle = tmpreangled + 135;
                        RotaryKnobView.this.reangled = tmpreangled;
                        RotaryKnobView.this.angle = (float) tmpangle;
                        RotaryKnobView.this.postInvalidate();
                    }
                }
                return true;
            }
        });
    }

    private void drawKnob(Canvas canvas) {
        int ang = (int) this.angle;
        if (ang < 135) {
            ang += 360;
        }
        int ang2 = (int) this.angle_shutter;
        if (ang2 < 135) {
            ang2 += 360;
        }
        if (this.on) {
            drawArc3(canvas, 135, ang2 - 135, this.knob_arc_bkg2);
        }
        drawBkgArc(canvas, 135, 270, this.knob_arc_bkg);
        if (this.on) {
            drawBkgArc2(canvas, 405, 90, this.knob_arc_bkg);
        }
        drawArc(canvas, 135, ang - 135, this.knob_arc);
    }

    private void drawArc(Canvas canvas, int startAngle, int sweepDegrees, Paint paint) {
        if (sweepDegrees > 0) {
            int diameter = Math.min(this.knob_background.getWidth(), this.knob_background.getHeight()) + ((this.margin / 4) * 2);
            int thickness = diameter / 4;
            int left = (this.width - diameter) / 2;
            int top = (this.height - diameter) / 2;
            RectF outerCircle = new RectF((float) left, (float) top, (float) (left + diameter), (float) (top + diameter));
            int innerDiameter = diameter - (thickness * 2);
            RectF innerCircle = new RectF((float) (left + thickness), (float) (top + thickness), (float) ((left + thickness) + innerDiameter), (float) ((top + thickness) + innerDiameter));
            this.path.reset();
            this.path.arcTo(outerCircle, (float) startAngle, (float) sweepDegrees);
            this.path.arcTo(innerCircle, (float) (startAngle + sweepDegrees), (float) (-sweepDegrees));
            this.path.close();
            canvas.drawPath(this.path, paint);
            if (this.reangled < 270 && this.drawLocalValue) {
                Canvas canvas2 = canvas;
                canvas2.drawTextOnPath("" + ((int) Math.round((((double) (this.reangled * (this.max - this.min))) / 270.0d) + ((double) this.min))) + this.unit, this.path, (((float) ((int) (((double) (outerCircle.width() / 2.0f)) * ((((double) this.reangled) * 3.141592653589793d) / 180.0d)))) - this.knob_arc_text.measureText("" + ((int) Math.round((((double) (this.reangled * (this.max - this.min))) / 270.0d) + ((double) this.min))) + this.unit)) - 1.0f, -8.0f, this.knob_arc_text);
            }
        }
    }

    private void drawBkgArc(Canvas canvas, int startAngle, int sweepDegrees, Paint paint) {
        if (sweepDegrees > 0) {
            int diameter = Math.min(this.knob_background.getWidth(), this.knob_background.getHeight()) + ((this.margin / 4) * 2);
            int thickness = diameter / 4;
            int left = (this.width - diameter) / 2;
            int top = (this.height - diameter) / 2;
            RectF outerCircle = new RectF((float) left, (float) top, (float) (left + diameter), (float) (top + diameter));
            int innerDiameter = diameter - (thickness * 2);
            RectF innerCircle = new RectF((float) (left + thickness), (float) (top + thickness), (float) ((left + thickness) + innerDiameter), (float) ((top + thickness) + innerDiameter));
            this.path.reset();
            this.path.arcTo(outerCircle, (float) startAngle, (float) sweepDegrees);
            this.path.arcTo(innerCircle, (float) (startAngle + sweepDegrees), (float) (-sweepDegrees));
            this.path.close();
            canvas.drawPath(this.path, paint);
        }
    }

    private void drawArc3(Canvas canvas, int startAngle, int sweepDegrees, Paint paint) {
        if (sweepDegrees > 0) {
            int diameter = Math.min(this.knob_background.getWidth(), this.knob_background.getHeight()) + ((this.margin / 4) * 4);
            int thickness = diameter / 4;
            int left = (this.width - diameter) / 2;
            int top = (this.height - diameter) / 2;
            RectF outerCircle = new RectF((float) left, (float) top, (float) (left + diameter), (float) (top + diameter));
            int innerDiameter = diameter - (thickness * 2);
            RectF innerCircle = new RectF((float) (left + thickness), (float) (top + thickness), (float) ((left + thickness) + innerDiameter), (float) ((top + thickness) + innerDiameter));
            this.path.reset();
            this.path.arcTo(outerCircle, (float) startAngle, (float) sweepDegrees);
            this.path.arcTo(innerCircle, (float) (startAngle + sweepDegrees), (float) (-sweepDegrees));
            this.path.close();
            canvas.drawPath(this.path, paint);
            if (this.reangled_shutter < 270) {
                Canvas canvas2 = canvas;
                canvas2.drawTextOnPath("" + ((int) Math.round((((double) (this.reangled_shutter * (this.max - this.min))) / 270.0d) + ((double) this.min))) + this.unit, this.path, (((float) ((int) (((double) (outerCircle.width() / 2.0f)) * ((((double) this.reangled_shutter) * 3.141592653589793d) / 180.0d)))) - this.knob_arc_text2.measureText("" + ((int) Math.round((((double) (this.reangled_shutter * (this.max - this.min))) / 270.0d) + ((double) this.min))) + this.unit)) - 1.0f, -8.0f, this.knob_arc_text2);
            }
        }
    }

    private void drawBkgArc2(Canvas canvas, int startAngle, int sweepDegrees, Paint paint) {
        if (sweepDegrees > 0) {
            int diameter = Math.min(this.knob_background.getWidth(), this.knob_background.getHeight()) + ((this.margin / 4) * 2);
            int thickness = diameter / 16;
            int left = (this.width - diameter) / 2;
            int top = (this.height - diameter) / 2;
            RectF outerCircle = new RectF((float) left, (float) top, (float) (left + diameter), (float) (top + diameter));
            int innerDiameter = diameter - (thickness * 2);
            RectF innerCircle = new RectF((float) (left + thickness), (float) (top + thickness), (float) ((left + thickness) + innerDiameter), (float) ((top + thickness) + innerDiameter));
            this.path.reset();
            this.path.arcTo(outerCircle, (float) startAngle, (float) sweepDegrees);
            this.path.arcTo(innerCircle, (float) (startAngle + sweepDegrees), (float) (-sweepDegrees));
            this.path.close();
            int path_len = (int) (((double) (outerCircle.width() / 2.0f)) * ((((double) sweepDegrees) * 3.141592653589793d) / 180.0d));
            canvas.drawTextOnPath("" + this.max + this.unit, this.path, 12.0f, (float) (thickness - 8), this.knob_arc_text);
            canvas.drawTextOnPath("" + this.min, this.path, (((float) path_len) - this.knob_arc_text.measureText("" + this.min)) - 12.0f, (float) (thickness - 8), this.knob_arc_text);
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

    public Bitmap getRotatedBitmap(Bitmap bm, int angle) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate((float) angle, (float) (width / 2), (float) (height / 2));
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private int pointToAngle(int x, int y) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        if (x >= centerX && y < centerY) {
            return ((int) Math.toDegrees(Math.atan(((double) (x - centerX)) / ((double) (centerY - y))))) + 270;
        }
        if (x > centerX && y >= centerY) {
            return (int) Math.toDegrees(Math.atan(((double) (y - centerY)) / ((double) (x - centerX))));
        }
        if (x <= centerX && y > centerY) {
            return ((int) Math.toDegrees(Math.atan(((double) (centerX - x)) / ((double) (y - centerY))))) + 90;
        }
        if (x < centerX && y <= centerY) {
            return ((int) Math.toDegrees(Math.atan(((double) (centerY - y)) / ((double) (centerX - x))))) + 180;
        }
        throw new IllegalArgumentException();
    }

    private void notifyListener(int arg, boolean on) {
        if (this.listener != null) {
            this.listener.onKnobChanged(arg, on);
        }
    }

    public void setBorderValues(int Min, int Max) {
        this.min = Min;
        this.max = Max;
    }

    public void setOn(boolean on) {
        this.on = on;
        postInvalidate();
    }

    public void setIndicator(boolean indicator) {
        this.indicator = indicator;
        postInvalidate();
    }

    public void setValue(int Value, boolean on, boolean move_knob, boolean indicator) {
        this.indicator = indicator;
        this.on = on;
        this.ON = on;
        setValue(Value, move_knob);
    }

    public void setValue(int Value, boolean on, boolean move_knob) {
        this.on = on;
        this.ON = on;
        setValue(Value, move_knob);
    }

    public void setValue(int Value, boolean move_knob) {
        if (Value == this.local_value) {
            showLocalValue(false);
        }
        int tmp_reangled = ((Value - this.min) * 270) / (this.max - this.min);
        float tmp_angle = (float) (tmp_reangled + 135);
        this.reangled_shutter = tmp_reangled;
        this.angle_shutter = tmp_angle;
        if (move_knob) {
            this.reangled = this.reangled_shutter;
            this.angle = this.angle_shutter;
        }
        if (this.onoff_with_value) {
            if (this.reangled_shutter <= 0) {
                this.on = false;
            } else {
                this.on = true;
            }
        }
        postInvalidate();
    }

    public void showLocalValue(boolean show) {
        this.drawLocalValue = show;
    }

    protected void onDraw(Canvas c) {
        c.getClipBounds(this.bounds);
        this.width = this.bounds.width();
        this.height = this.bounds.height();
        if (this.on) {
            this.knob_arc_text.setColorFilter(null);
            this.knob_arc_bkg.setColorFilter(null);
            this.knob_arc.setColorFilter(null);
            this.knob_bitmaps.setColorFilter(null);
        } else {
            this.knob_arc_text.setColorFilter(this.greyScaleFilter);
            this.knob_arc_bkg.setColorFilter(this.greyScaleFilter);
            this.knob_arc.setColorFilter(this.greyScaleFilter);
            this.knob_bitmaps.setColorFilter(this.greyScaleFilter);
        }
        drawKnob(c);
        c.drawBitmap(this.knob_groove, (float) ((this.width / 2) - (this.knob_groove.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_groove.getHeight() / 2)), this.knob_bitmaps);
        if (this.knobIsPushed) {
            c.drawBitmap(this.knob_background_push, (float) ((this.width / 2) - (this.knob_background_push.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_background_push.getHeight() / 2)), this.knob_bitmaps);
        } else {
            c.drawBitmap(this.knob_background, (float) ((this.width / 2) - (this.knob_background.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_background.getHeight() / 2)), this.knob_bitmaps);
        }
        if (this.indicator) {
            if (this.knobIsPushed) {
                c.drawBitmap(this.knob_diode_on_push, (float) ((this.width / 2) - (this.knob_diode_on_push.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_diode_on_push.getHeight() / 2)), this.knob_bitmaps);
            } else {
                c.drawBitmap(this.knob_diode_on, (float) ((this.width / 2) - (this.knob_diode_on.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_diode_on.getHeight() / 2)), this.knob_bitmaps);
            }
        } else if (this.knobIsPushed) {
            c.drawBitmap(this.knob_diode_off_push, (float) ((this.width / 2) - (this.knob_diode_off_push.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_diode_off_push.getHeight() / 2)), this.knob_bitmaps);
        } else {
            c.drawBitmap(this.knob_diode_off, (float) ((this.width / 2) - (this.knob_diode_off.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_diode_off.getHeight() / 2)), this.knob_bitmaps);
        }
        c.translate((float) this.bounds.left, (float) this.bounds.top);
        c.rotate(this.angle + 90.0f, ((float) this.width) / 2.0f, ((float) this.height) / 2.0f);
        if (this.knobIsPushed) {
            c.drawBitmap(this.knob_pointer_push, (float) ((this.width / 2) - (this.knob_pointer_push.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_pointer_push.getHeight() / 2)), this.knob_bitmaps);
        } else {
            c.drawBitmap(this.knob_pointer, (float) ((this.width / 2) - (this.knob_pointer.getWidth() / 2)), (float) ((this.height / 2) - (this.knob_pointer.getHeight() / 2)), this.knob_bitmaps);
        }
        super.onDraw(c);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minr = Math.min(getMeasuredWidth(), getMeasuredWidth());
        setMeasuredDimension(minr, minr);
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();
        this.normal_divider = 3;
        this.pushed_divider = 30;
        this.margin = (this.width / this.normal_divider) / 2;
        this.bounds = new Rect();
        this.knob_arc.setColor(this.localValueColor);
        this.knob_arc.setAntiAlias(true);
        this.knob_arc_bkg.setColor(this.localBkgColor);
        this.knob_arc_bkg.setAntiAlias(true);
        this.knob_arc_bkg2.setColor(this.targetBkgColor);
        this.knob_arc_bkg2.setAntiAlias(true);
        this.knob_arc_text.setColor(this.localValueColor);
        this.knob_arc_text.setAntiAlias(true);
        this.knob_arc_text.setTextSize((float) (this.margin / 4));
        this.knob_arc_text2.setColor(this.targetValueColor);
        this.knob_arc_text2.setAntiAlias(true);
        this.knob_arc_text2.setTextSize((float) (this.margin / 2));
        this.knob_bitmaps.setAntiAlias(true);
        this.knob_bitmaps.setFilterBitmap(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0f);
        this.greyScaleFilter = new ColorMatrixColorFilter(cm);
        if (this.knob_style == 1) {
            this.knob_groove = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.termostat_rowek), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
            this.knob_background = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.termostat_galka), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
            this.knob_background_push = getResizedBitmap(this.knob_background, this.knob_background.getWidth() - (this.knob_background.getWidth() / this.pushed_divider), this.knob_background.getHeight() - (this.knob_background.getHeight() / this.pushed_divider));
            this.knob_pointer = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.termostat_wskaznik), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
            this.knob_pointer_push = getResizedBitmap(this.knob_pointer, this.knob_pointer.getWidth() - (this.knob_pointer.getWidth() / this.pushed_divider), this.knob_pointer.getHeight() - (this.knob_pointer.getHeight() / this.pushed_divider));
            this.knob_diode_off = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.termostat_off), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
            this.knob_diode_off_push = getResizedBitmap(this.knob_diode_off, this.knob_diode_off.getWidth() - (this.knob_diode_off.getWidth() / this.pushed_divider), this.knob_diode_off.getHeight() - (this.knob_diode_off.getHeight() / this.pushed_divider));
            this.knob_diode_on = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.termostat_on), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
            this.knob_diode_on_push = getResizedBitmap(this.knob_diode_on, this.knob_diode_on.getWidth() - (this.knob_diode_on.getWidth() / this.pushed_divider), this.knob_diode_on.getHeight() - (this.knob_diode_on.getHeight() / this.pushed_divider));
            return;
        }
        this.knob_groove = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sciemniacz_rowek), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
        this.knob_background = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sciemniacz_galka), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
        this.knob_background_push = getResizedBitmap(this.knob_background, this.knob_background.getWidth() - (this.knob_background.getWidth() / this.pushed_divider), this.knob_background.getHeight() - (this.knob_background.getHeight() / this.pushed_divider));
        this.knob_pointer = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sciemniacz_wskaznik), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
        this.knob_pointer_push = getResizedBitmap(this.knob_pointer, this.knob_pointer.getWidth() - (this.knob_pointer.getWidth() / this.pushed_divider), this.knob_pointer.getHeight() - (this.knob_pointer.getHeight() / this.pushed_divider));
        this.knob_diode_off = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sciemniacz_off), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
        this.knob_diode_off_push = getResizedBitmap(this.knob_diode_off, this.knob_diode_off.getWidth() - (this.knob_diode_off.getWidth() / this.pushed_divider), this.knob_diode_off.getHeight() - (this.knob_diode_off.getHeight() / this.pushed_divider));
        this.knob_diode_on = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sciemniacz_on), this.height - (this.height / this.normal_divider), this.width - (this.width / this.normal_divider));
        this.knob_diode_on_push = getResizedBitmap(this.knob_diode_on, this.knob_diode_on.getWidth() - (this.knob_diode_on.getWidth() / this.pushed_divider), this.knob_diode_on.getHeight() - (this.knob_diode_on.getHeight() / this.pushed_divider));
    }
}
