package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.widget.ImageView;
import nexovision.android.nexwell.eu.nexovision.R;

public class ECRoundSliderClipper extends ImageView {
    private int angle;
    Bitmap bmp;

    public ECRoundSliderClipper(Context context) {
        super(context);
        init();
    }

    public ECRoundSliderClipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ECRoundSliderClipper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.angle = 0;
    }

    public void setAngle(int a) {
        this.angle = a;
        postInvalidate();
    }

    public void setImageRes(int res) {
        this.bmp = BitmapFactory.decodeResource(getResources(), res);
        postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
        RectF rect = new RectF((float) (getLeft() + getPaddingLeft()), (float) (getTop() + getPaddingLeft()), (float) (getRight() - getPaddingRight()), (float) (getBottom() - getPaddingBottom()));
        int a = (int) (rect.width() / 10.0f);
        rect.left += (float) a;
        rect.top += (float) a;
        rect.right -= (float) a;
        rect.bottom -= (float) a;
        Paint paint = new Paint();
        paint.setStrokeWidth(rect.width() / 4.0f);
        paint.setStrokeCap(Cap.BUTT);
        paint.setStyle(Style.STROKE);
        paint.setFlags(1);
        if (this.bmp == null) {
            this.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.circle_green);
        }
        if (this.bmp != null) {
            paint.setShader(new BitmapShader(this.bmp, TileMode.CLAMP, TileMode.CLAMP));
        } else {
            SweepGradient gradient = new SweepGradient(0.0f, 0.0f, getResources().getColor(R.color.gray), getResources().getColor(R.color.yellow_light));
            Matrix m = new Matrix();
            m.postRotate(134.0f);
            m.postTranslate(rect.left + (rect.width() / 2.0f), rect.top + (rect.height() / 2.0f));
            gradient.setLocalMatrix(m);
            paint.setShader(gradient);
        }
        canvas.drawArc(rect, 135.0f, (float) this.angle, false, paint);
    }
}
