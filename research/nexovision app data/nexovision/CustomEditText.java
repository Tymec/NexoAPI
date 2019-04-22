package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import nexovision.android.nexwell.eu.nexovision.R;

public class CustomEditText extends LinearLayout {
    private int backgroundColor;
    private View hline;
    private EditText input;
    private LinearLayout layout;
    private int widgetColor;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
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
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.widget_custom_edittext, this, true);
        setOrientation(1);
        setLayoutParams(new LayoutParams(-1, -2));
        this.layout = (LinearLayout) getChildAt(0);
        this.hline = getChildAt(1);
        this.layout.setBackgroundColor(this.backgroundColor);
        this.input = (EditText) ((TextInputLayout) this.layout.getChildAt(0)).getChildAt(0);
        ((TextInputLayout) this.layout.getChildAt(0)).setHint(labelText);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.input.setEnabled(enabled);
    }

    public void setLabel(CharSequence labelText) {
        this.input.setHint(labelText);
    }

    public void setLabel(@StringRes int labelTextId) {
        ((TextInputLayout) this.layout.getChildAt(0)).setHint(getResources().getString(labelTextId));
    }

    public CharSequence getLabel() {
        return ((TextInputLayout) this.layout.getChildAt(0)).getHint();
    }

    public void setText(CharSequence text) {
        this.input.setText(text);
    }

    public CharSequence getText() {
        return this.input.getText();
    }
}
