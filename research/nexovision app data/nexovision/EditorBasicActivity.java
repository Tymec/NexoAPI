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
import eu.nexwell.android.nexovision.model.Blind;
import eu.nexwell.android.nexovision.model.BlindGroup;
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.ISwitch;
import eu.nexwell.android.nexovision.model.NVModel;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorBasicActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private CustomCheckbox checkboxInvertedLogic;
    private EditText inputName;
    private EditText inputResource;
    private CustomSpinner spinnerCategory;
    private CustomSpinner spinnerPlace;

    /* renamed from: eu.nexwell.android.nexovision.EditorBasicActivity$1 */
    class C19061 implements OnClickListener {
        C19061() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT != null) {
                if (EditorBasicActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                    Snackbar.make(MainActivity.fragment, EditorBasicActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                    EditorBasicActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorBasicActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorBasicActivity.this.saveFormToElementModel(EditorBasicActivity.TEMP_ELEMENT)) {
                NVModel.addElement(EditorBasicActivity.TEMP_ELEMENT);
                ((Category) NVModel.getCategories().get(EditorBasicActivity.this.spinnerCategory.getSelectedItemPosition())).addElement(EditorBasicActivity.TEMP_ELEMENT);
                Snackbar.make(view, EditorBasicActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                EditorBasicActivity.this.finish();
            } else {
                Snackbar.make(view, EditorBasicActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_basic);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputResource = (EditText) findViewById(R.id.input_resource);
        this.checkboxInvertedLogic = (CustomCheckbox) findViewById(R.id.checkbox_invertedlogic);
        this.spinnerCategory = (CustomSpinner) findViewById(R.id.spinner_category);
        this.spinnerPlace = (CustomSpinner) findViewById(R.id.spinner_place);
        this.spinnerCategory.setAdapter(new ArrayAdapter(this, R.layout.spinner_item, NVModel.getCategoriesNames()));
        ArrayList<String> places = NVModel.getElementNamesByType(NVModel.EL_TYPE_SET);
        places.add(0, getString(R.string.EditorActivity_Form_NoPlaces));
        this.spinnerPlace.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, places));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19061());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((ISwitch) TEMP_ELEMENT).setResource(((ISwitch) NVModel.CURR_ELEMENT).getResource());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorBasicActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputResource.setText(((ISwitch) NVModel.CURR_ELEMENT).getResource());
            if (NVModel.CURR_ELEMENT instanceof Blind) {
                this.checkboxInvertedLogic.setVisibility(0);
                this.checkboxInvertedLogic.setChecked(((Blind) NVModel.CURR_ELEMENT).isLogicInverted());
            } else if (NVModel.CURR_ELEMENT instanceof BlindGroup) {
                this.checkboxInvertedLogic.setVisibility(0);
                this.checkboxInvertedLogic.setChecked(((BlindGroup) NVModel.CURR_ELEMENT).isLogicInverted());
            } else {
                this.checkboxInvertedLogic.setVisibility(8);
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
        this.checkboxInvertedLogic.setVisibility(8);
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
        if (element instanceof Blind) {
            ((Blind) element).setInvertedLogic(this.checkboxInvertedLogic.isChecked());
        }
        if (element instanceof BlindGroup) {
            ((BlindGroup) element).setInvertedLogic(this.checkboxInvertedLogic.isChecked());
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
        if (this.inputResource.getText().toString() == null || this.inputResource.getText().toString().isEmpty()) {
            return true;
        }
        ((ISwitch) element).setResource(this.inputResource.getText().toString());
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
