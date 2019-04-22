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
import android.widget.ArrayAdapter;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Thermometer;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorCategoryTemperatureActivity extends AppCompatActivity {
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private CustomSpinner spinnerThermometer1;
    private CustomSpinner spinnerThermometer2;

    /* renamed from: eu.nexwell.android.nexovision.EditorCategoryTemperatureActivity$1 */
    class C19101 implements OnClickListener {
        C19101() {
        }

        public void onClick(View view) {
            if (EditorCategoryTemperatureActivity.this.saveFormToModel()) {
                Snackbar.make(MainActivity.fragment, EditorCategoryTemperatureActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                EditorCategoryTemperatureActivity.this.finish();
                return;
            }
            Snackbar.make(view, EditorCategoryTemperatureActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_category_temperature);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.spinnerThermometer1 = (CustomSpinner) findViewById(R.id.spinner_thermometer1);
        this.spinnerThermometer2 = (CustomSpinner) findViewById(R.id.spinner_thermometer2);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19101());
        }
        ArrayList<String> ths = NVModel.getElementNamesByType(NVModel.EL_TYPE_THERMOMETER);
        ths.add(0, "(no thermometer)");
        this.spinnerThermometer1.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, ths));
        this.spinnerThermometer2.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, ths));
        ArrayList<IElement> thermometers = NVModel.getElementsByType(NVModel.EL_TYPE_THERMOMETER);
        if (NVModel.getMainOutThermometer() != null) {
            this.spinnerThermometer1.setSelection(thermometers.indexOf(NVModel.getMainOutThermometer()) + 1);
        }
        if (NVModel.getMainInThermometer() != null) {
            this.spinnerThermometer2.setSelection(thermometers.indexOf(NVModel.getMainInThermometer()) + 1);
        }
    }

    private boolean saveFormToModel() {
        int position1 = this.spinnerThermometer1.getSelectedItemPosition();
        if (position1 < 1) {
            NVModel.setMainOutThermometer(null);
        } else {
            NVModel.setMainOutThermometer((Thermometer) NVModel.getElementsByType(NVModel.EL_TYPE_THERMOMETER).get(position1 - 1));
        }
        int position2 = this.spinnerThermometer2.getSelectedItemPosition();
        if (position2 < 1) {
            NVModel.setMainInThermometer(null);
        } else {
            NVModel.setMainInThermometer((Thermometer) NVModel.getElementsByType(NVModel.EL_TYPE_THERMOMETER).get(position2 - 1));
        }
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
