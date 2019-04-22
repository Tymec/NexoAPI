package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import nexovision.android.nexwell.eu.nexovision.R;

public class CustomEntry extends LinearLayout {
    private int backgroundColor;
    private View hline;
    private TextView label;
    private LinearLayout layout;
    private TextView value;
    private int widgetColor;

    public CustomEntry(Context context) {
        this(context, null);
    }

    public CustomEntry(Context context, AttributeSet attrs) {
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
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.widget_custom_entry, this, true);
        setOrientation(1);
        setLayoutParams(new LayoutParams(-1, -2));
        this.layout = (LinearLayout) getChildAt(0);
        this.hline = getChildAt(1);
        this.label = (TextView) this.layout.getChildAt(0);
        this.label.setText(labelText);
        this.label.setTextColor(this.widgetColor);
        this.layout.setBackgroundColor(this.backgroundColor);
        this.value = (TextView) this.layout.getChildAt(1);
        if (this.value.getText() == null || this.value.getText().toString().isEmpty()) {
            this.value.setText(this.label.getText().toString());
            this.value.setTextColor(this.widgetColor);
            this.label.setVisibility(4);
            return;
        }
        this.value.setTextColor(getResources().getColor(17170435));
        this.label.setVisibility(0);
    }

    public TextView getLabel() {
        return this.label;
    }

    public TextView getValue() {
        return this.value;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.value.setEnabled(enabled);
        this.label.setEnabled(enabled);
    }

    public void setText(CharSequence labelText) {
        this.label.setText(labelText);
        if (this.value.getText() == null || this.value.getText().toString().isEmpty()) {
            this.value.setText(this.label.getText().toString());
            this.value.setTextColor(this.widgetColor);
            this.label.setVisibility(4);
            return;
        }
        this.value.setTextColor(getResources().getColor(17170435));
        this.label.setVisibility(0);
    }

    public void setText(@StringRes int labelTextId) {
        this.label.setText(getResources().getString(labelTextId));
        if (this.value.getText() == null || this.value.getText().toString().isEmpty()) {
            this.value.setText(this.label.getText().toString());
            this.value.setTextColor(this.widgetColor);
            this.label.setVisibility(4);
            return;
        }
        this.value.setTextColor(getResources().getColor(17170435));
        this.label.setVisibility(0);
    }

    public CharSequence getText() {
        return this.label.getText();
    }

    public void setColor(@ColorRes int colorRes) {
        this.label.setTextColor(getResources().getColor(colorRes));
    }

    public int getColor() {
        return this.widgetColor;
    }

    public void setValueText(CharSequence valueText) {
        this.value.setText(valueText);
        if (this.value.getText() == null || this.value.getText().toString().isEmpty()) {
            this.value.setText(this.label.getText().toString());
            this.value.setTextColor(this.widgetColor);
            this.label.setVisibility(4);
            return;
        }
        this.value.setTextColor(getResources().getColor(17170435));
        this.label.setVisibility(0);
    }

    public CharSequence getValueText() {
        return this.value.getText();
    }
}
