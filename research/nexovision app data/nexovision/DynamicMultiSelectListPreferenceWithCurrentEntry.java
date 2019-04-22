package eu.nexwell.android.nexovision;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.Set;

public class DynamicMultiSelectListPreferenceWithCurrentEntry extends MultiSelectListPreference {
    private Builder builder;
    private C1904x3ed2f879 mOnNeutralButtonClicListner;
    private C1905x9087d6db mOnPositiveButtonClicListner;
    private String negativeButtonText;
    private String neutralButtonText;
    private String positiveButtonText;

    /* renamed from: eu.nexwell.android.nexovision.DynamicMultiSelectListPreferenceWithCurrentEntry$2 */
    class C19012 implements OnClickListener {
        C19012() {
        }

        public void onClick(DialogInterface dialog, int which) {
            DynamicMultiSelectListPreferenceWithCurrentEntry.this.onPositiveButtonClick();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.DynamicMultiSelectListPreferenceWithCurrentEntry$3 */
    class C19023 implements OnClickListener {
        C19023() {
        }

        public void onClick(DialogInterface dialog, int which) {
            DynamicMultiSelectListPreferenceWithCurrentEntry.this.onNeutralButtonClick();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.DynamicMultiSelectListPreferenceWithCurrentEntry$4 */
    class C19034 implements OnClickListener {
        C19034() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.DynamicMultiSelectListPreferenceWithCurrentEntry$DynamicListPreferenceWithCurrentEntryOnNeutralButtonClickListener */
    public interface C1904x3ed2f879 {
        void onNeutralButtonClick(DynamicMultiSelectListPreferenceWithCurrentEntry dynamicMultiSelectListPreferenceWithCurrentEntry);
    }

    /* renamed from: eu.nexwell.android.nexovision.DynamicMultiSelectListPreferenceWithCurrentEntry$DynamicListPreferenceWithCurrentEntryOnPositiveButtonClickListener */
    public interface C1905x9087d6db {
        void onPositiveButtonClick(DynamicMultiSelectListPreferenceWithCurrentEntry dynamicMultiSelectListPreferenceWithCurrentEntry);
    }

    public DynamicMultiSelectListPreferenceWithCurrentEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onPositiveButtonClick() {
        if (this.mOnPositiveButtonClicListner != null) {
            this.mOnPositiveButtonClicListner.onPositiveButtonClick(this);
        }
        super.onClick();
    }

    protected void onNeutralButtonClick() {
        if (this.mOnNeutralButtonClicListner != null) {
            this.mOnNeutralButtonClicListner.onNeutralButtonClick(this);
        }
        super.onClick();
    }

    public void setOnPositiveButtonClickListner(C1905x9087d6db l) {
        this.mOnPositiveButtonClicListner = l;
    }

    public void setOnNeutralButtonClickListner(C1904x3ed2f879 l) {
        this.mOnNeutralButtonClicListner = l;
    }

    public void setPositiveButtonText(CharSequence positiveButtonText) {
        this.positiveButtonText = positiveButtonText.toString();
    }

    public void setNeutralButtonText(CharSequence neutralButtonText) {
        this.neutralButtonText = neutralButtonText.toString();
    }

    public void setNegativeButtonText(CharSequence negativeButtonText) {
        this.negativeButtonText = negativeButtonText.toString();
    }

    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ((AlertDialog) getDialog()).getButton(-3).setEnabled(false);
        final ListView lv = (ListView) view.findViewById(16908298);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (lv.getCount() == 1) {
                    Log.d("DMSLPWCE", "BUTTON_NEUTRAL).setEnabled(true)");
                    ((AlertDialog) DynamicMultiSelectListPreferenceWithCurrentEntry.this.getDialog()).getButton(-3).setEnabled(true);
                    return;
                }
                Log.d("DMSLPWCE", "BUTTON_NEUTRAL).setEnabled(false)");
                ((AlertDialog) DynamicMultiSelectListPreferenceWithCurrentEntry.this.getDialog()).getButton(-3).setEnabled(false);
            }
        });
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        this.builder = builder;
        builder.setPositiveButton(this.positiveButtonText, new C19012());
        builder.setNeutralButton(this.neutralButtonText, new C19023());
        builder.setNegativeButton(this.negativeButtonText, new C19034());
    }

    public void setValues(Set<String> values) {
        super.setValues(values);
        notifyChanged();
    }
}
