package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class SquareImageButton extends ImageButton {
    private String squareBy = null;

    public SquareImageButton(Context context) {
        super(context);
    }

    public SquareImageButton(Context context, AttributeSet attrs) {
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

    public SquareImageButton(Context context, AttributeSet attrs, int defStyle) {
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
