package eu.nexwell.android.nexovision;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import java.util.List;
import nexovision.android.nexwell.eu.nexovision.R;

public class CheckListAdapter extends ArrayAdapter<String> {
    public SparseBooleanArray checkList;
    private boolean checkbox_en;
    private Context context;
    private List<String> labelList;
    private CheckListAdapterListener listener;
    private int resource;
    private List<String> valueList;

    /* renamed from: eu.nexwell.android.nexovision.CheckListAdapter$1 */
    class C18961 implements Runnable {
        C18961() {
        }

        public void run() {
            CheckListAdapter.this.notifyDataSetChanged();
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.CheckListAdapter$2 */
    class C18972 implements OnCheckedChangeListener {
        C18972() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                CheckListAdapter.this.checkList.put(((Integer) buttonView.getTag()).intValue(), true);
                CheckListAdapter.this.notifyListener(true);
                return;
            }
            CheckListAdapter.this.checkList.delete(((Integer) buttonView.getTag()).intValue());
            CheckListAdapter.this.notifyListener(false);
        }
    }

    public interface CheckListAdapterListener {
        void onCheckChanged(boolean z);
    }

    public CheckListAdapter(Context ctx, int resource, List<String> labelList, List<String> valueList, boolean checkbox_en) {
        super(ctx, resource, labelList);
        this.checkbox_en = false;
        this.resource = resource;
        this.context = ctx;
        this.checkList = new SparseBooleanArray();
        this.labelList = labelList;
        this.valueList = valueList;
        this.checkbox_en = checkbox_en;
    }

    public CheckListAdapter(Context ctx, List<String> labelList, List<String> valueList, boolean checkbox_en) {
        super(ctx, R.layout.checklist_simpleitem, labelList);
        this.checkbox_en = false;
        this.resource = R.layout.checklist_simpleitem;
        this.context = ctx;
        this.checkList = new SparseBooleanArray();
        this.labelList = labelList;
        this.valueList = valueList;
        this.checkbox_en = checkbox_en;
    }

    public void refresh() {
        MainActivity.handler.post(new C18961());
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(this.resource, parent, false);
        }
        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        checkbox.setTag(Integer.valueOf(position));
        checkbox.setChecked(this.checkList.get(position));
        checkbox.setOnCheckedChangeListener(new C18972());
        TextView label = (TextView) convertView.findViewById(R.id.label);
        TextView value = (TextView) convertView.findViewById(R.id.value);
        if (!(this.labelList == null || this.labelList.get(position) == null)) {
            label.setText((CharSequence) this.labelList.get(position));
        }
        if (value != null) {
            if (this.valueList == null || this.valueList.get(position) == null) {
                value.setText("");
            } else {
                value.setText((CharSequence) this.valueList.get(position));
            }
        }
        return convertView;
    }

    public void setItemChecked(int position, boolean checked) {
        if (checked) {
            this.checkList.put(position, true);
            notifyListener(true);
            return;
        }
        this.checkList.delete(position);
        notifyListener(false);
    }

    public SparseBooleanArray getCheckedItemPositions() {
        return this.checkList;
    }

    public void setCheckListAdapterListener(CheckListAdapterListener l) {
        this.listener = l;
    }

    private void notifyListener(boolean checked) {
        if (this.listener != null) {
            this.listener.onCheckChanged(checked);
        }
    }
}
