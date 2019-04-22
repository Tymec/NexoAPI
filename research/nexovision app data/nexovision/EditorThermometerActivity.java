package eu.nexwell.android.nexovision;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Thermometer;
import eu.nexwell.android.nexovision.model.Thermostat;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorThermometerActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private EditText inputName;
    private EditText inputResource;
    private EditText inputTempMax;
    private EditText inputTempMin;
    private CustomSpinner spinnerCategory;
    private CustomSpinner spinnerPlace;
    private CustomSpinner spinnerThermostate;

    /* renamed from: eu.nexwell.android.nexovision.EditorThermometerActivity$1 */
    class C19311 implements OnClickListener {
        C19311() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT == null) {
                EditorThermometerActivity.TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_THERMOMETER);
                if (EditorThermometerActivity.this.saveFormToElementModel(EditorThermometerActivity.TEMP_ELEMENT)) {
                    NVModel.addElement(EditorThermometerActivity.TEMP_ELEMENT);
                    ((Category) NVModel.getCategories().get(EditorThermometerActivity.this.spinnerCategory.getSelectedItemPosition())).addElement(EditorThermometerActivity.TEMP_ELEMENT);
                    Snackbar.make(view, EditorThermometerActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                    EditorThermometerActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorThermometerActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorThermometerActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                Snackbar.make(MainActivity.fragment, EditorThermometerActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                EditorThermometerActivity.this.finish();
            } else {
                Snackbar.make(view, EditorThermometerActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.EditorThermometerActivity$3 */
    class C19333 implements OnItemSelectedListener {
        C19333() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Thermostat t = (Thermostat) NVModel.getElementsByType(NVModel.EL_TYPE_THERMOSTAT).get(position);
            EditorThermometerActivity.this.inputTempMin.setText("" + t.getMin());
            EditorThermometerActivity.this.inputTempMax.setText("" + t.getMax());
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_thermometer);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputResource = (EditText) findViewById(R.id.input_resource);
        this.spinnerThermostate = (CustomSpinner) findViewById(R.id.spinner_thermostate);
        this.inputTempMin = (EditText) findViewById(R.id.input_tempmin);
        this.inputTempMax = (EditText) findViewById(R.id.input_tempmax);
        this.spinnerCategory = (CustomSpinner) findViewById(R.id.spinner_category);
        this.spinnerPlace = (CustomSpinner) findViewById(R.id.spinner_place);
        this.spinnerCategory.setAdapter(new ArrayAdapter(this, R.layout.spinner_item, NVModel.getCategoriesNames()));
        ArrayList<String> places = NVModel.getElementNamesByType(NVModel.EL_TYPE_SET);
        places.add(0, getString(R.string.EditorActivity_Form_NoPlaces));
        this.spinnerPlace.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, places));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19311());
        }
        if (NVModel.CURR_ELEMENT != null) {
            Thermostat t;
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((ISwitch) TEMP_ELEMENT).setResource(((ISwitch) NVModel.CURR_ELEMENT).getResource());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorThermometerActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputResource.setText(((ISwitch) NVModel.CURR_ELEMENT).getResource());
            ArrayList<String> thermostates = NVModel.getElementNamesByType(NVModel.EL_TYPE_THERMOSTAT);
            if (thermostates.isEmpty()) {
                this.spinnerThermostate.setVisibility(8);
            } else {
                if (thermostates.size() < 1) {
                    thermostates.add(getString(R.string.EditorActivity_Form_NoThermometers));
                }
                this.spinnerThermostate.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, thermostates));
                this.spinnerThermostate.setOnItemSelectedListener(new C19333());
                t = ((Thermometer) NVModel.CURR_ELEMENT).getThermostat();
                if (t != null) {
                    int index = NVModel.getElementsByType(NVModel.EL_TYPE_THERMOSTAT).indexOf(t);
                    if (index >= 0) {
                        this.spinnerThermostate.setSelection(index);
                    }
                }
                this.spinnerThermostate.setVisibility(0);
            }
            t = ((Thermometer) NVModel.CURR_ELEMENT).getThermostat();
            if (t != null) {
                this.inputTempMin.setText("" + t.getMin());
                this.inputTempMax.setText("" + t.getMax());
            }
            this.spinnerCategory.setSelection(NVModel.getCategories().indexOf(NVModel.getCategory(NVModel.getElementTypeDefaultCategory(NVModel.CURR_ELEMENT.getType()))));
            ArrayList<ISet> sets = NVModel.getSetsByElementId(TEMP_ELEMENT.getId().intValue());
            if (sets == null || sets.size() <= 0) {
                this.spinnerPlace.setSelection(0);
            } else {
                this.spinnerPlace.setSelection(NVModel.getElementsByType(NVModel.EL_TYPE_SET).indexOf(sets.get(0)) + 1);
            }
            fab.setImageResource(R.drawable.ic_save);
            return;
        }
        fab.setImageResource(R.drawable.ic_add);
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
        if (this.spinnerThermostate.getChildCount() > 1 && NVModel.getElementsByType(NVModel.EL_TYPE_THERMOSTAT).size() > 0) {
            IElement t = (IElement) NVModel.getElementsByType(NVModel.EL_TYPE_THERMOSTAT).get(this.spinnerThermostate.getSelectedItemPosition());
            if (t instanceof Thermostat) {
                ((Thermostat) t).setMin(Float.valueOf(Float.parseFloat(this.inputTempMin.getText().toString())));
                ((Thermostat) t).setMax(Float.valueOf(Float.parseFloat(this.inputTempMax.getText().toString())));
                ((Thermometer) element).setThermostat((Thermostat) t);
            }
        }
        Thermostat t2 = ((Thermometer) element).getThermostat();
        if (t2 != null) {
            t2.setMin(Float.valueOf(Float.parseFloat(this.inputTempMin.getText().toString())));
            t2.setMax(Float.valueOf(Float.parseFloat(this.inputTempMax.getText().toString())));
        }
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
