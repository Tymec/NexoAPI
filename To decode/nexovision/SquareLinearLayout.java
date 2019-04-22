package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import nexovision.android.nexwell.eu.nexovision.R;

public class SquareLinearLayout extends LinearLayout {
    private String squareBy = null;

    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyle) {
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
