package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import nexovision.android.nexwell.eu.nexovision.R;

public class RoundedSquareImageView extends ImageView {
    private int mCornerRadius = 0;
    private Paint mMaskPaint = new Paint(1);
    private Path mMaskPath;
    private String squareBy = null;

    public RoundedSquareImageView(Context context) {
        super(context);
        init();
    }

    public RoundedSquareImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RoundedSquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{R.attr.squareBy});
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.attr.squareBy:
                    this.squareBy = a.getString(attr);
                    break;
                default:
                    break;
            }
        }
        init();
    }

    private void init() {
        ViewCompat.setLayerType(this, 1, null);
        this.mMaskPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.mCornerRadius = (int) getResources().getDimension(R.dimen.icon_radius);
    }

    public void setCornerRadius(int cornerRadius) {
        this.mCornerRadius = cornerRadius;
        generateMaskPath(getWidth(), getWidth());
        invalidate();
    }

    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (w != oldW || h != oldH) {
            generateMaskPath(w, h);
        }
    }

    private void generateMaskPath(int w, int h) {
        this.mMaskPath = new Path();
        this.mMaskPath.addRoundRect(new RectF(0.0f, 0.0f, (float) w, (float) h), new float[]{(float) this.mCornerRadius, (float) this.mCornerRadius, (float) this.mCornerRadius, (float) this.mCornerRadius, (float) this.mCornerRadius, (float) this.mCornerRadius, (float) this.mCornerRadius, (float) this.mCornerRadius}, Direction.CW);
        this.mMaskPath.setFillType(FillType.INVERSE_WINDING);
    }

    protected void onDraw(Canvas canvas) {
        if (canvas.isOpaque()) {
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), 255, 4);
        }
        super.onDraw(canvas);
        if (this.mMaskPath != null) {
            canvas.drawPath(this.mMaskPath, this.mMaskPaint);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.squareBy == null || this.squareBy.equals("width")) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        } else {
            setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
        }
    }
}
