package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class DynamicImageView extends ImageView {
    public DynamicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (d.getIntrinsicHeight() / d.getIntrinsicWidth() > height / width) {
                width = (int) Math.ceil((double) ((((float) height) * ((float) d.getIntrinsicWidth())) / ((float) d.getIntrinsicHeight())));
            } else {
                height = (int) Math.ceil((double) ((((float) width) * ((float) d.getIntrinsicHeight())) / ((float) d.getIntrinsicWidth())));
            }
            setMeasuredDimension(width, height);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
