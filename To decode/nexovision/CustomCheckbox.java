package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import nexovision.android.nexwell.eu.nexovision.R;

public class CustomCheckbox extends LinearLayout {
    private int backgroundColor;
    private CheckBox checkbox;
    private View hline;
    private LinearLayout layout;
    private int widgetColor;

    public CustomCheckbox(Context context) {
        this(context, null);
    }

    public CustomCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.widgetColor = Color.parseColor("#FF6D6D6D");
        this.backgroundColor = Color.parseColor("#00000000");
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        String labelText = "";
        TypedArray a = context.obtainStyledAttributes(attrs, C2072R.styleable.CustomWidget);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    this.backgroundColor = a.getColor(attr, this.backgroundColor);
                    break;
                case 1:
                    labelText = a.getString(attr);
                    break;
                case 2:
                    this.widgetColor = a.getColor(attr, this.widgetColor);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.widget_custom_checkbox, this, true);
        setOrientation(1);
        setLayoutParams(new LayoutParams(-1, -2));
        this.layout = (LinearLayout) getChildAt(0);
        this.hline = getChildAt(1);
        this.checkbox = (CheckBox) this.layout.getChildAt(0);
        this.checkbox.setText(labelText);
        this.checkbox.setTextColor(this.widgetColor);
        this.layout.setBackgroundColor(this.backgroundColor);
    }

    public CheckBox getCheckBox() {
        return this.checkbox;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.checkbox.setEnabled(enabled);
    }

    public void setChecked(boolean checked) {
        this.checkbox.setChecked(checked);
    }

    public boolean isChecked() {
        return this.checkbox.isChecked();
    }

    public void setText(CharSequence labelText) {
        this.checkbox.setText(labelText);
    }

    public void setText(@StringRes int labelTextId) {
        this.checkbox.setText(getResources().getString(labelTextId));
    }

    public CharSequence getText() {
        return this.checkbox.getText();
    }

    public void setColor(@ColorRes int colorRes) {
        this.checkbox.setTextColor(getResources().getColor(colorRes));
    }

    public int getColor() {
        return this.widgetColor;
    }
}
