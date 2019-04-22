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
import android.widget.EditText;
import eu.nexwell.android.nexovision.model.CameraIP;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.NVModel;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorCameraActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private EditText inputAddress;
    private EditText inputName;
    private EditText inputSizeX;
    private EditText inputSizeY;
    private CustomSpinner spinnerPlace;

    /* renamed from: eu.nexwell.android.nexovision.EditorCameraActivity$1 */
    class C19081 implements OnClickListener {
        C19081() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT == null) {
                EditorCameraActivity.TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_CAMERA);
                if (EditorCameraActivity.this.saveFormToElementModel(EditorCameraActivity.TEMP_ELEMENT)) {
                    NVModel.addElement(EditorCameraActivity.TEMP_ELEMENT);
                    NVModel.getCategory(NVModel.CATEGORY_CAMERAS).addElement(EditorCameraActivity.TEMP_ELEMENT);
                    Snackbar.make(view, EditorCameraActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                    EditorCameraActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorCameraActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorCameraActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                Snackbar.make(MainActivity.fragment, EditorCameraActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                EditorCameraActivity.this.finish();
            } else {
                Snackbar.make(view, EditorCameraActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_camera);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputAddress = (EditText) findViewById(R.id.input_address);
        this.inputSizeX = (EditText) findViewById(R.id.input_size_x);
        this.inputSizeY = (EditText) findViewById(R.id.input_size_y);
        this.spinnerPlace = (CustomSpinner) findViewById(R.id.spinner_place);
        ArrayList<String> places = NVModel.getElementNamesByType(NVModel.EL_TYPE_SET);
        places.add(0, getString(R.string.EditorActivity_Form_NoPlaces));
        this.spinnerPlace.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, places));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19081());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((CameraIP) TEMP_ELEMENT).setAddress(((CameraIP) NVModel.CURR_ELEMENT).getAddress());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorCameraActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputAddress.setText(((CameraIP) NVModel.CURR_ELEMENT).getAddress());
            this.inputSizeX.setText(((CameraIP) NVModel.CURR_ELEMENT).getSize().x + "");
            this.inputSizeY.setText(((CameraIP) NVModel.CURR_ELEMENT).getSize().y + "");
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
        if (el == null) {
            return false;
        }
        if (!(this.inputName.getText().toString() == null || this.inputName.getText().toString().isEmpty())) {
            el.setName(this.inputName.getText().toString());
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
            if (!(this.inputAddress.getText().toString() == null || this.inputAddress.getText().toString().isEmpty())) {
                ((CameraIP) el).setAddress(this.inputAddress.getText().toString().replaceAll("\\s", ""));
                if (!(this.inputSizeX.getText().toString() == null || this.inputSizeX.getText().toString().isEmpty() || this.inputSizeY.getText().toString() == null || this.inputSizeY.getText().toString().isEmpty())) {
                    ((CameraIP) el).setSize(Integer.parseInt(this.inputSizeX.getText().toString().replaceAll("\\s", "")), Integer.parseInt(this.inputSizeY.getText().toString().replaceAll("\\s", "")));
                    return true;
                }
            }
        }
        return false;
    }

    public static Context getContext() {
        return context;
    }
}
