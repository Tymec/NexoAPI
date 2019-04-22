package eu.nexwell.android.nexovision;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import eu.nexwell.android.nexovision.CheckListAdapter.CheckListAdapterListener;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Sensor;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class SensorsListActivity extends AppCompatActivity implements CheckListAdapterListener {
    private static Context context;
    private static FloatingActionButton fab;
    public static Handler handler;
    private ListView listSensors;

    /* renamed from: eu.nexwell.android.nexovision.SensorsListActivity$2 */
    class C20862 implements OnClickListener {
        C20862() {
        }

        public void onClick(View view) {
            if (EditorPartitionActivity.TEMP_ELEMENT != null && (EditorPartitionActivity.TEMP_ELEMENT instanceof Partition)) {
                ((Partition) EditorPartitionActivity.TEMP_ELEMENT).clearSensors();
                SparseBooleanArray chkd_sensors = ((CheckListAdapter) SensorsListActivity.this.listSensors.getAdapter()).getCheckedItemPositions();
                ArrayList<IElement> sensors = NVModel.getElementsByType(NVModel.EL_TYPE_SENSOR);
                for (int i = 0; i < sensors.size(); i++) {
                    if (chkd_sensors.get(i)) {
                        ((Partition) EditorPartitionActivity.TEMP_ELEMENT).addSensor((Sensor) sensors.get(i));
                    }
                }
                SensorsListActivity.this.finish();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_sensorslist);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.listSensors = (ListView) findViewById(R.id.list_sensors);
        ArrayList<String> ids = new ArrayList();
        Iterator<IElement> itre = NVModel.getElements().iterator();
        while (itre.hasNext()) {
            IElement el = (IElement) itre.next();
            if (el.getType() != null && el.getType().equals(NVModel.EL_TYPE_SENSOR)) {
                ids.add(el.getId() + "");
            }
        }
        final CheckListAdapter adapter = new CheckListAdapter(getContext(), NVModel.getElementNamesByType(NVModel.EL_TYPE_SENSOR), ids, true);
        adapter.setCheckListAdapterListener(this);
        this.listSensors.setAdapter(adapter);
        this.listSensors.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                adapter.checkList.put(arg2, !Boolean.valueOf(adapter.checkList.get(arg2)).booleanValue());
                adapter.refresh();
            }
        });
        ArrayList<IElement> sensors = NVModel.getElementsByType(NVModel.EL_TYPE_SENSOR);
        Iterator<IElement> itrs = ((Partition) EditorPartitionActivity.TEMP_ELEMENT).getSensors().iterator();
        while (itrs.hasNext()) {
            adapter.checkList.put(sensors.indexOf((Sensor) itrs.next()), true);
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C20862());
        }
    }

    public static Context getContext() {
        return context;
    }

    public void onCheckChanged(boolean checked) {
    }
}
