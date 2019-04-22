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
import eu.nexwell.android.nexovision.model.Category;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.ISet;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import java.util.ArrayList;
import java.util.Iterator;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorLogicActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private EditText inputAction1;
    private EditText inputAction2;
    private EditText inputEvent1;
    private EditText inputEvent2;
    private EditText inputName;
    private EditText inputState1Label;
    private EditText inputState2Label;
    private CustomSpinner spinnerPlace;

    /* renamed from: eu.nexwell.android.nexovision.EditorLogicActivity$1 */
    class C19151 implements OnClickListener {
        C19151() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT == null) {
                EditorLogicActivity.TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_LOGIC);
                if (EditorLogicActivity.this.saveFormToElementModel(EditorLogicActivity.TEMP_ELEMENT)) {
                    NVModel.addElement(EditorLogicActivity.TEMP_ELEMENT);
                    NVModel.getCategory(NVModel.CATEGORY_LOGICS).addElement(EditorLogicActivity.TEMP_ELEMENT);
                    Snackbar.make(view, EditorLogicActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                    EditorLogicActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorLogicActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorLogicActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                Snackbar.make(MainActivity.fragment, EditorLogicActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                EditorLogicActivity.this.finish();
            } else {
                Snackbar.make(view, EditorLogicActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_logic);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputAction1 = (EditText) findViewById(R.id.input_action1);
        this.inputEvent1 = (EditText) findViewById(R.id.input_event1);
        this.inputState1Label = (EditText) findViewById(R.id.input_state1_label);
        this.inputAction2 = (EditText) findViewById(R.id.input_action2);
        this.inputEvent2 = (EditText) findViewById(R.id.input_event2);
        this.inputState2Label = (EditText) findViewById(R.id.input_state2_label);
        this.spinnerPlace = (CustomSpinner) findViewById(R.id.spinner_place);
        ArrayList<String> places = NVModel.getElementNamesByType(NVModel.EL_TYPE_SET);
        places.add(0, getString(R.string.EditorActivity_Form_NoPlaces));
        this.spinnerPlace.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, places));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19151());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((Logic) TEMP_ELEMENT).setAction1(((Logic) NVModel.CURR_ELEMENT).getAction1());
            ((Logic) TEMP_ELEMENT).setEvent1(((Logic) NVModel.CURR_ELEMENT).getEvent1());
            ((Logic) TEMP_ELEMENT).setState1Label(((Logic) NVModel.CURR_ELEMENT).getState1Label());
            ((Logic) TEMP_ELEMENT).setAction2(((Logic) NVModel.CURR_ELEMENT).getAction2());
            ((Logic) TEMP_ELEMENT).setEvent2(((Logic) NVModel.CURR_ELEMENT).getEvent2());
            ((Logic) TEMP_ELEMENT).setState2Label(((Logic) NVModel.CURR_ELEMENT).getState2Label());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorLogicActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputAction1.setText(((Logic) NVModel.CURR_ELEMENT).getAction1());
            this.inputEvent1.setText(((Logic) NVModel.CURR_ELEMENT).getEvent1());
            this.inputState1Label.setText(((Logic) NVModel.CURR_ELEMENT).getState1Label());
            this.inputAction2.setText(((Logic) NVModel.CURR_ELEMENT).getAction2());
            this.inputEvent2.setText(((Logic) NVModel.CURR_ELEMENT).getEvent2());
            this.inputState2Label.setText(((Logic) NVModel.CURR_ELEMENT).getState2Label());
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
        if (!(this.inputAction1.getText().toString() == null || this.inputAction1.getText().toString().isEmpty())) {
            ((Logic) element).setAction1(this.inputAction1.getText().toString());
        }
        if (!(this.inputEvent1.getText().toString() == null || this.inputEvent1.getText().toString().isEmpty())) {
            ((Logic) element).setEvent1(this.inputEvent1.getText().toString());
        }
        if (!(this.inputState1Label.getText().toString() == null || this.inputState1Label.getText().toString().isEmpty())) {
            ((Logic) element).setState1Label(this.inputState1Label.getText().toString());
        }
        if (!(this.inputAction2.getText().toString() == null || this.inputAction2.getText().toString().isEmpty())) {
            ((Logic) element).setAction2(this.inputAction2.getText().toString());
        }
        if (!(this.inputEvent2.getText().toString() == null || this.inputEvent2.getText().toString().isEmpty())) {
            ((Logic) element).setEvent2(this.inputEvent2.getText().toString());
        }
        if (!(this.inputState2Label.getText().toString() == null || this.inputState2Label.getText().toString().isEmpty())) {
            ((Logic) element).setState2Label(this.inputState2Label.getText().toString());
        }
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
