package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

public class SquareButton extends Button {
    private String squareBy = null;

    public SquareButton(Context context) {
        super(context);
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C2072R.styleable.SquareButton);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    this.squareBy = a.getString(attr);
                    break;
                default:
                    break;
            }
        }
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.squareBy == null || this.squareBy.equals("height")) {
            setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
        } else {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }
    }
}
