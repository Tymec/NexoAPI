package eu.nexwell.android.nexovision;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class DynamicListPreferenceWithCurrentEntry extends ListPreference {
    private DynamicListPreferenceWithCurrentEntryOnClickListener mOnClicListner;

    public interface DynamicListPreferenceWithCurrentEntryOnClickListener {
        void onClick(DynamicListPreferenceWithCurrentEntry dynamicListPreferenceWithCurrentEntry);
    }

    public DynamicListPreferenceWithCurrentEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onClick() {
        if (this.mOnClicListner != null) {
            this.mOnClicListner.onClick(this);
        }
        super.onClick();
    }

    public void setOnClickListner(DynamicListPreferenceWithCurrentEntryOnClickListener l) {
        this.mOnClicListner = l;
    }

    public CharSequence getSummary() {
        CharSequence entry = getEntry();
        CharSequence summary = super.getSummary();
        if (summary == null || summary.length() <= 0) {
            if (entry == null || entry.length() <= 0) {
                return null;
            }
            return entry.toString();
        } else if (entry == null || entry.length() <= 0) {
            return summary.toString();
        } else {
            return String.format("%s %s", new Object[]{entry, summary.toString()});
        }
    }

    public void setValue(String value) {
        super.setValue(value);
        notifyChanged();
    }
}
