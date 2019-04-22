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
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.widget.ImageView;
import nexovision.android.nexwell.eu.nexovision.R;

public class ECRectSliderClipper extends ImageView {
    private int angle;
    Bitmap bmp;

    public ECRectSliderClipper(Context context) {
        super(context);
        init();
    }

    public ECRectSliderClipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ECRectSliderClipper(Context context, AttributeSet attrs, int defStyle) {
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
        RectF rect = new RectF((float) getLeft(), (float) getTop(), (float) getRight(), (float) getBottom());
        Paint paint = new Paint();
        paint.setStrokeWidth(1.0f);
        paint.setStrokeCap(Cap.SQUARE);
        paint.setStyle(Style.FILL);
        paint.setFlags(1);
        if (this.bmp == null) {
            this.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.circle_green);
        }
        if (this.bmp != null) {
            paint.setShader(new BitmapShader(this.bmp, TileMode.CLAMP, TileMode.CLAMP));
        } else {
            SweepGradient gradient = new SweepGradient(0.0f, 0.0f, new int[]{-16776961, SupportMenu.CATEGORY_MASK}, new float[]{0.0f, 1.0f});
            Matrix m = new Matrix();
            m.postRotate(135.0f);
            m.postTranslate(rect.left + (rect.width() / 2.0f), rect.top + (rect.height() / 2.0f));
            gradient.setLocalMatrix(m);
            paint.setShader(gradient);
        }
        canvas.drawArc(rect, 135.0f, (float) this.angle, true, paint);
    }
}
