package eu.nexwell.android.nexovision;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class DynamicListPreference extends ListPreference {
    private DynamicListPreferenceOnClickListener mOnClicListner;

    public interface DynamicListPreferenceOnClickListener {
        void onClick(DynamicListPreference dynamicListPreference);
    }

    public DynamicListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onClick() {
        if (this.mOnClicListner != null) {
            this.mOnClicListner.onClick(this);
        }
        super.onClick();
    }

    public void setOnClickListner(DynamicListPreferenceOnClickListener l) {
        this.mOnClicListner = l;
    }
}
