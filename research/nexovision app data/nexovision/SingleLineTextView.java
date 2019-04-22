package eu.nexwell.android.nexovision;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class SingleLineTextView extends TextView {
    public SingleLineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSingleLine();
        setEllipsize(TruncateAt.END);
    }

    public SingleLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine();
        setEllipsize(TruncateAt.END);
    }

    public SingleLineTextView(Context context) {
        super(context);
        setSingleLine();
        setEllipsize(TruncateAt.END);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Layout layout = getLayout();
        if (layout != null) {
            int lineCount = layout.getLineCount();
            if (lineCount > 0 && layout.getEllipsisCount(lineCount - 1) > 0) {
                setTextSize(0, getTextSize() - 1.0f);
                setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }
}
