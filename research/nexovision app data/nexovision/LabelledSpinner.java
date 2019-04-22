package eu.nexwell.android.nexovision;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import nexovision.android.nexwell.eu.nexovision.R;

public class LabelledSpinner extends LinearLayout {
    private TextView label;
    private Spinner spinner;
    private int widgetColor;

    public LabelledSpinner(Context context) {
        this(context, null);
    }

    public LabelledSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.widgetColor = Color.parseColor("#FF6D6D6D");
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        String labelText = "";
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.widget_labelled_spinner, this, true);
        setOrientation(1);
        setLayoutParams(new LayoutParams(-1, -2));
        this.label = (TextView) getChildAt(0);
        this.label.setText(labelText);
        this.label.setTextColor(this.widgetColor);
        Log.d("LabelledSpinner", "text=" + labelText);
        this.spinner = (Spinner) getChildAt(1);
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

    public void alignLabelWithSpinnerItem(boolean indentLabel) {
        if (indentLabel) {
            alignLabelWithSpinnerItem(8);
        } else {
            alignLabelWithSpinnerItem(4);
        }
    }

    private void alignLabelWithSpinnerItem(int indentDps) {
        MarginLayoutParams labelParams = (MarginLayoutParams) this.label.getLayoutParams();
        labelParams.leftMargin = dpToPixels(indentDps);
        labelParams.rightMargin = dpToPixels(indentDps);
        this.label.setLayoutParams(labelParams);
    }

    private int dpToPixels(int dps) {
        if (dps == 0) {
            return 0;
        }
        return (int) ((((float) dps) * getResources().getDisplayMetrics().density) + 0.5f);
    }
}
