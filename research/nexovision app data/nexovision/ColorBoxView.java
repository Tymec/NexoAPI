package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;

public class ColorBoxView extends ImageView {
    private int COLOR_LOCAL = Color.rgb(200, 220, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    private int COLOR_REMOTE = Color.rgb(Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 240, 200);
    private Rect bounds;
    private Paint cb_content_Paint = new Paint();
    private RectF cb_content_Rect = new RectF();
    private Paint cb_frame_Paint = new Paint();
    private RectF cb_frame_Rect = new RectF();
    private int color = ViewCompat.MEASURED_STATE_MASK;
    private int color2 = ViewCompat.MEASURED_STATE_MASK;
    private int frame_weight = 1;
    private int height = 1;
    private int hmargin = 1;
    private ColorBoxListener listener;
    private boolean on = false;
    private final Path path = new Path();
    private int style = 0;
    private int vmargin = 1;
    private int width = 1;

    /* renamed from: eu.nexwell.android.nexovision.ColorBoxView$1 */
    class C18981 implements OnClickListener {
        C18981() {
        }

        public void onClick(View v) {
            ColorBoxView.this.notifyListener(ColorBoxView.this.color);
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.ColorBoxView$2 */
    class C18992 implements OnLongClickListener {
        C18992() {
        }

        public boolean onLongClick(View v) {
            ColorBoxView.this.notifyListener2();
            return true;
        }
    }

    public interface ColorBoxListener {
        void onColorBoxChanged(int i);

        void onColorBoxRequest();
    }

    public void setColorBoxListener(ColorBoxListener l) {
        this.listener = l;
    }

    public ColorBoxView(Context context) {
        super(context);
    }

    public ColorBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setStyle(int s) {
        this.style = s;
        postInvalidate();
    }

    public void initialize() {
        this.bounds = new Rect();
        this.cb_frame_Paint.setColor(Color.rgb(90, 90, 90));
        this.cb_frame_Paint.setAntiAlias(true);
        this.cb_content_Paint.setAntiAlias(true);
        setOnClickListener(new C18981());
        setOnLongClickListener(new C18992());
    }

    private void drawColorBox(Canvas canvas, int color) {
        canvas.drawOval(this.cb_frame_Rect, this.cb_frame_Paint);
        this.cb_content_Paint.setColor(color);
        canvas.drawOval(this.cb_content_Rect, this.cb_content_Paint);
    }

    private void drawDoubledColorBox(Canvas canvas, int local_color, int remote_color) {
        float round_x = this.cb_frame_Rect.width() / 10.0f;
        float round_y = this.cb_frame_Rect.height() / 10.0f;
        float LR_frame_size = (this.cb_frame_Rect.height() - this.cb_content_Rect.height()) / 4.0f;
        if (this.on) {
            this.COLOR_LOCAL = Color.rgb(200, 220, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            this.COLOR_REMOTE = Color.rgb(Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 240, 200);
            this.cb_frame_Paint.setAlpha(255);
        } else {
            this.COLOR_LOCAL = Color.rgb(128, 128, 128);
            this.COLOR_REMOTE = Color.rgb(160, 160, 160);
            this.cb_frame_Paint.setAlpha(32);
        }
        this.cb_frame_Paint.setColor(this.COLOR_LOCAL);
        canvas.drawRoundRect(this.cb_frame_Rect, round_x, round_y, this.cb_frame_Paint);
        this.cb_frame_Paint.setColor(Color.rgb(90, 90, 90));
        canvas.drawRoundRect(new RectF(this.cb_frame_Rect.left + LR_frame_size, this.cb_frame_Rect.top + LR_frame_size, this.cb_frame_Rect.right - LR_frame_size, this.cb_frame_Rect.bottom - LR_frame_size), round_x, round_y, this.cb_frame_Paint);
        this.cb_content_Paint.setColor(local_color);
        canvas.drawRoundRect(this.cb_content_Rect, round_x, round_y, this.cb_content_Paint);
        this.path.reset();
        this.path.moveTo(this.cb_frame_Rect.left + (this.cb_frame_Rect.width() / 2.0f), this.cb_frame_Rect.top + ((this.cb_frame_Rect.height() * 2.0f) / 8.0f));
        this.path.lineTo(this.cb_frame_Rect.left + (this.cb_frame_Rect.width() / 3.0f), this.cb_frame_Rect.top);
        this.path.lineTo(this.cb_frame_Rect.left + ((this.cb_frame_Rect.width() * 2.0f) / 3.0f), this.cb_frame_Rect.top);
        this.path.close();
        this.cb_frame_Paint.setColor(this.COLOR_LOCAL);
        canvas.drawPath(this.path, this.cb_frame_Paint);
        canvas.clipRect(new Rect((int) this.cb_frame_Rect.left, (int) (this.cb_frame_Rect.bottom - (this.cb_frame_Rect.height() / 2.0f)), (int) this.cb_frame_Rect.right, (int) this.cb_frame_Rect.bottom));
        this.cb_frame_Paint.setColor(this.COLOR_REMOTE);
        canvas.drawRoundRect(this.cb_frame_Rect, round_x, round_y, this.cb_frame_Paint);
        this.cb_frame_Paint.setColor(Color.rgb(90, 90, 90));
        canvas.drawRoundRect(new RectF(this.cb_frame_Rect.left + LR_frame_size, this.cb_frame_Rect.top + LR_frame_size, this.cb_frame_Rect.right - LR_frame_size, this.cb_frame_Rect.bottom - LR_frame_size), round_x, round_y, this.cb_frame_Paint);
        this.cb_content_Paint.setColor(remote_color);
        canvas.drawRoundRect(this.cb_content_Rect, round_x, round_y, this.cb_content_Paint);
        this.path.reset();
        this.path.moveTo(this.cb_frame_Rect.left + (this.cb_frame_Rect.width() / 2.0f), this.cb_frame_Rect.bottom - ((this.cb_frame_Rect.height() * 2.0f) / 8.0f));
        this.path.lineTo(this.cb_frame_Rect.left + (this.cb_frame_Rect.width() / 3.0f), this.cb_frame_Rect.bottom);
        this.path.lineTo(this.cb_frame_Rect.left + ((this.cb_frame_Rect.width() * 2.0f) / 3.0f), this.cb_frame_Rect.bottom);
        this.path.close();
        this.cb_frame_Paint.setColor(this.COLOR_REMOTE);
        canvas.drawPath(this.path, this.cb_frame_Paint);
    }

    private void notifyListener(int arg) {
        if (this.listener != null) {
            this.listener.onColorBoxChanged(arg);
        }
    }

    private void notifyListener2() {
        if (this.listener != null) {
            this.listener.onColorBoxRequest();
        }
    }

    public void setValue(int Value) {
        this.color = Value;
        postInvalidate();
    }

    public void setValue2(int Value) {
        this.color2 = Value;
        postInvalidate();
    }

    public void setOn(boolean on) {
        this.on = on;
        postInvalidate();
    }

    public int getValue() {
        return this.color;
    }

    public int getValue2() {
        return this.color2;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, widthSize);
        Log.d("ColorBoxViewU", "onMeasure width:" + this.height + " height:" + this.height);
    }

    protected void onDraw(Canvas c) {
        c.getClipBounds(this.bounds);
        Log.d("ColorBoxViewU", "bounds.H=" + this.bounds.height() + ", bounds.W=" + this.bounds.width());
        this.width = Math.min(this.bounds.width(), this.bounds.height());
        this.height = Math.min(this.bounds.width(), this.bounds.height());
        this.bounds.set(this.bounds.left + ((this.bounds.width() - this.width) / 2), this.bounds.top, this.bounds.left + this.width, this.bounds.top + this.height);
        this.hmargin = 0;
        this.vmargin = 0;
        this.frame_weight = this.width / 10;
        this.cb_frame_Rect.left = (float) (this.bounds.left + getPaddingLeft());
        this.cb_frame_Rect.top = (float) (this.bounds.top + getPaddingTop());
        this.cb_frame_Rect.right = (float) ((this.bounds.left + this.width) - getPaddingRight());
        this.cb_frame_Rect.bottom = (float) ((this.bounds.top + this.height) - getPaddingBottom());
        this.cb_content_Rect.left = this.cb_frame_Rect.left + ((float) this.frame_weight);
        this.cb_content_Rect.top = this.cb_frame_Rect.top + ((float) this.frame_weight);
        this.cb_content_Rect.right = this.cb_frame_Rect.right - ((float) this.frame_weight);
        this.cb_content_Rect.bottom = this.cb_frame_Rect.bottom - ((float) this.frame_weight);
        if (this.style == 1) {
            drawDoubledColorBox(c, this.color, this.color2);
        } else {
            drawColorBox(c, this.color);
        }
        super.onDraw(c);
    }
}
