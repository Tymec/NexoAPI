package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import nexovision.android.nexwell.eu.nexovision.R;

public class CustomSpinner extends LinearLayout {
    private int backgroundColor;
    private View hline;
    private TextView label;
    private LinearLayout layout;
    private Spinner spinner;
    private int widgetColor;

    public CustomSpinner(Context context) {
        this(context, null);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
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
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.widget_custom_spinner, this, true);
        setOrientation(1);
        setLayoutParams(new LayoutParams(-1, -2));
        this.layout = (LinearLayout) getChildAt(0);
        this.hline = getChildAt(1);
        this.label = (TextView) this.layout.getChildAt(0);
        this.label.setText(labelText);
        this.label.setTextColor(this.widgetColor);
        this.layout.setBackgroundColor(this.backgroundColor);
        this.spinner = (Spinner) this.layout.getChildAt(1);
    }

    public TextView getLabel() {
        return this.label;
    }

    public Spinner getSpinner() {
        return this.spinner;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.spinner.setEnabled(enabled);
        this.label.setEnabled(enabled);
    }

    public void setText(CharSequence labelText) {
        this.label.setText(labelText);
    }

    public void setText(@StringRes int labelTextId) {
        this.label.setText(getResources().getString(labelTextId));
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

    public void setAdapter(SpinnerAdapter adapter) {
        this.spinner.setAdapter(adapter);
    }

    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        this.spinner.setOnItemSelectedListener(listener);
    }

    public void setSelection(int selection) {
        this.spinner.setSelection(selection);
    }

    public int getSelectedItemPosition() {
        return this.spinner.getSelectedItemPosition();
    }
}
