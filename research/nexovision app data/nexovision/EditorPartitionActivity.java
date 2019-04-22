package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Partition;
import eu.nexwell.android.nexovision.model.Partition.Function;
import eu.nexwell.android.nexovision.model.Sensor;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorPartitionActivity extends AppCompatActivity {
    public static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private CustomEntry entrySensors;
    private EditText inputName;
    private EditText inputResource;
    private CustomSpinner spinnerCategory;
    private CustomSpinner spinnerPlace;
    private CustomSpinner spinnerUse;

    /* renamed from: eu.nexwell.android.nexovision.EditorPartitionActivity$1 */
    class C19171 implements OnClickListener {
        C19171() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT == null) {
                EditorPartitionActivity.TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_PARTITION);
                if (EditorPartitionActivity.this.saveFormToElementModel(EditorPartitionActivity.TEMP_ELEMENT)) {
                    NVModel.addElement(EditorPartitionActivity.TEMP_ELEMENT);
                    ((Category) NVModel.getCategories().get(EditorPartitionActivity.this.spinnerCategory.getSelectedItemPosition())).addElement(EditorPartitionActivity.TEMP_ELEMENT);
                    Snackbar.make(view, EditorPartitionActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                    EditorPartitionActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorPartitionActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorPartitionActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                Snackbar.make(MainActivity.fragment, EditorPartitionActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                EditorPartitionActivity.this.finish();
            } else {
                Snackbar.make(view, EditorPartitionActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.EditorPartitionActivity$3 */
    class C19193 implements OnClickListener {
        C19193() {
        }

        public void onClick(View v) {
            Intent intent = new Intent().setClass(EditorPartitionActivity.getContext(), SensorsListActivity.class);
            intent.addFlags(67108864);
            EditorPartitionActivity.this.startActivityForResult(intent, 0);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_partition);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.spinnerUse = (CustomSpinner) findViewById(R.id.spinner_use);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputResource = (EditText) findViewById(R.id.input_resource);
        this.spinnerCategory = (CustomSpinner) findViewById(R.id.spinner_category);
        this.spinnerPlace = (CustomSpinner) findViewById(R.id.spinner_place);
        this.entrySensors = (CustomEntry) findViewById(R.id.entry_sensors);
        ArrayList<String> useNamesList = new ArrayList();
        Iterator<Function> itru = Partition._funclist.iterator();
        while (itru.hasNext()) {
            useNamesList.add(getContext().getString(((Function) itru.next()).getResLabel()));
        }
        this.spinnerUse.setAdapter(new ArrayAdapter(this, R.layout.spinner_item, useNamesList));
        this.spinnerCategory.setAdapter(new ArrayAdapter(this, R.layout.spinner_item, NVModel.getCategoriesNames()));
        ArrayList<String> places = NVModel.getElementNamesByType(NVModel.EL_TYPE_SET);
        places.add(0, getString(R.string.EditorActivity_Form_NoPlaces));
        this.spinnerPlace.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, places));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19171());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((ISwitch) TEMP_ELEMENT).setResource(((ISwitch) NVModel.CURR_ELEMENT).getResource());
            ((Partition) TEMP_ELEMENT).setFunc(((Partition) NVModel.CURR_ELEMENT).getFunc());
            ((Partition) TEMP_ELEMENT).clearSensors();
            ((Partition) TEMP_ELEMENT).addSensors(((Partition) NVModel.CURR_ELEMENT).getSensors());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorPartitionActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputResource.setText(((ISwitch) NVModel.CURR_ELEMENT).getResource());
            ArrayList<String> ids = new ArrayList();
            Iterator<IElement> itre = NVModel.getElements().iterator();
            while (itre.hasNext()) {
                IElement el = (IElement) itre.next();
                if (el.getType() != null && el.getType().equals(NVModel.EL_TYPE_SENSOR)) {
                    ids.add(el.getId() + "");
                }
            }
            this.spinnerUse.setSelection(((Partition) TEMP_ELEMENT).getFunc().getValue());
            this.spinnerCategory.setSelection(NVModel.getCategories().indexOf(NVModel.getCategory(NVModel.getElementTypeDefaultCategory(NVModel.CURR_ELEMENT.getType()))));
            ArrayList<ISet> sets = NVModel.getSetsByElementId(TEMP_ELEMENT.getId().intValue());
            if (sets == null || sets.size() <= 0) {
                this.spinnerPlace.setSelection(0);
            } else {
                this.spinnerPlace.setSelection(NVModel.getElementsByType(NVModel.EL_TYPE_SET).indexOf(sets.get(0)) + 1);
            }
            fab.setImageResource(R.drawable.ic_save);
        } else {
            fab.setImageResource(R.drawable.ic_add);
        }
        String sensors = null;
        Iterator<IElement> itrs = ((Partition) TEMP_ELEMENT).getSensors().iterator();
        while (itrs.hasNext()) {
            Sensor s = (Sensor) itrs.next();
            if (sensors == null) {
                sensors = s.getName();
            } else {
                sensors = sensors + ", " + s.getName();
            }
        }
        this.entrySensors.setValueText(sensors);
        this.entrySensors.setOnClickListener(new C19193());
    }

    protected void onResume() {
        super.onResume();
        String sensors = null;
        Iterator<IElement> itrs = ((Partition) TEMP_ELEMENT).getSensors().iterator();
        while (itrs.hasNext()) {
            Sensor s = (Sensor) itrs.next();
            if (sensors == null) {
                sensors = s.getName();
            } else {
                sensors = sensors + ", " + s.getName();
            }
        }
        this.entrySensors.setValueText(sensors);
    }

    private boolean saveFormToElementModel(IElement el) {
        IElement element = el;
        if (this.inputName.getText().toString() == null || this.inputName.getText().toString().isEmpty()) {
            return false;
        }
        element.setName(this.inputName.getText().toString());
        if (el != TEMP_ELEMENT) {
            el.setBackgrounds(TEMP_ELEMENT.getBackgrounds());
        }
        if (this.spinnerPlace.getSelectedItemPosition() <= 0) {
            Iterator<ISet> itrs = NVModel.getSetsByElementId(el.getId().intValue()).iterator();
            while (itrs.hasNext()) {
                ISet set = (ISet) itrs.next();
                if (!(set instanceof Category)) {
                    set.removeElement(el);
                }
            }
        } else {
            ISet place = (ISet) NVModel.getElementsByType(NVModel.EL_TYPE_SET).get(this.spinnerPlace.getSelectedItemPosition() - 1);
            if (!(place == null || place.getElements().contains(el))) {
                place.addElement(el);
            }
        }
        if (!(this.inputResource.getText().toString() == null || this.inputResource.getText().toString().isEmpty())) {
            ((ISwitch) element).setResource(this.inputResource.getText().toString());
        }
        ((Partition) el).setFunc((Function) Partition._funclist.get(this.spinnerUse.getSelectedItemPosition()));
        ((Partition) el).clearSensors();
        ((Partition) el).addSensors(((Partition) TEMP_ELEMENT).getSensors());
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
