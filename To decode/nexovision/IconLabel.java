package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class IconLabel extends TextView {
    public IconLabel(Context context) {
        super(context);
    }

    public IconLabel(Context context, AttributeSet attributeset) {
        super(context, attributeset);
    }

    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    public boolean isFocused() {
        return true;
    }
}
