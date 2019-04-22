package eu.nexwell.android.nexovision;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class EditTextPreferenceWithCurrentEntry extends EditTextPreference {
    private EditTextPreferenceWithCurrentEntryOnClickListener mOnClicListner;
    private boolean password = false;

    public interface EditTextPreferenceWithCurrentEntryOnClickListener {
        void onClick(EditTextPreferenceWithCurrentEntry editTextPreferenceWithCurrentEntry);
    }

    public EditTextPreferenceWithCurrentEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPasswordModeEnabled(boolean enabled) {
        this.password = enabled;
    }

    protected void onClick() {
        if (this.mOnClicListner != null) {
            this.mOnClicListner.onClick(this);
        }
        super.onClick();
    }

    public void setOnClickListner(EditTextPreferenceWithCurrentEntryOnClickListener l) {
        this.mOnClicListner = l;
    }

    public CharSequence getSummary() {
        CharSequence entry = getText();
        CharSequence summary = super.getSummary();
        if (entry == null || entry.length() <= 0) {
            if (summary == null || summary.length() <= 0) {
                return null;
            }
            return summary.toString();
        } else if (this.password) {
            return "****";
        } else {
            return entry.toString();
        }
    }

    public void setText(String text) {
        super.setText(text);
        notifyChanged();
    }
}
