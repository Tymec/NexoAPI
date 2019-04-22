package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import nexovision.android.nexwell.eu.nexovision.R;

public class SquareImageView extends ImageView {
    private String squareBy = null;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
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
