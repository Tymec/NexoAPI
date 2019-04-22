package eu.nexwell.android.nexovision;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.VideophoneIP;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorVideophoneActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private Editor editor;
    private EditText inputAddress;
    private EditText inputName;
    private EditText inputSipProxy;
    private SharedPreferences sharedPrefs;

    /* renamed from: eu.nexwell.android.nexovision.EditorVideophoneActivity$1 */
    class C19341 implements OnClickListener {
        C19341() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT == null) {
                EditorVideophoneActivity.TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_VIDEOPHONE);
                if (EditorVideophoneActivity.this.saveFormToElementModel(EditorVideophoneActivity.TEMP_ELEMENT)) {
                    Log.e("EVideophoneActivity", "TEMP_ELEMENT=" + EditorVideophoneActivity.TEMP_ELEMENT);
                    NVModel.addElement(EditorVideophoneActivity.TEMP_ELEMENT);
                    NVModel.getCategory(NVModel.CATEGORY_VIDEOPHONES).addElement(EditorVideophoneActivity.TEMP_ELEMENT);
                    Snackbar.make(view, EditorVideophoneActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                    EditorVideophoneActivity.this.editor.putString("pref_VidIP", EditorVideophoneActivity.this.inputAddress.getText().toString());
                    EditorVideophoneActivity.this.editor.putString("pref_VIdSIP", EditorVideophoneActivity.this.inputSipProxy.getText().toString());
                    EditorVideophoneActivity.this.editor.commit();
                    EditorVideophoneActivity.this.finish();
                    return;
                }
                Log.e("EVideophoneActivity", "Videophone NOT created!");
                Snackbar.make(view, EditorVideophoneActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorVideophoneActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                Snackbar.make(MainActivity.fragment, EditorVideophoneActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                if (NVModel.CURR_ELEMENT instanceof VideophoneIP) {
                    EditorVideophoneActivity.this.editor.putString("pref_VidIP", EditorVideophoneActivity.this.inputAddress.getText().toString());
                    EditorVideophoneActivity.this.editor.putString("pref_VIdSIP", EditorVideophoneActivity.this.inputSipProxy.getText().toString());
                    EditorVideophoneActivity.this.editor.commit();
                }
                EditorVideophoneActivity.this.finish();
            } else {
                Snackbar.make(view, EditorVideophoneActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = this.sharedPrefs.edit();
        setContentView(R.layout.activity_editor_videophone);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputAddress = (EditText) findViewById(R.id.input_address);
        this.inputSipProxy = (EditText) findViewById(R.id.input_sipproxy);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19341());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((VideophoneIP) TEMP_ELEMENT).setAddress(((VideophoneIP) NVModel.CURR_ELEMENT).getAddress());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorVideophoneActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputAddress.setText(((VideophoneIP) NVModel.CURR_ELEMENT).getAddress());
            this.inputSipProxy.setText(((VideophoneIP) NVModel.CURR_ELEMENT).getSipProxy());
            fab.setImageResource(R.drawable.ic_save);
            return;
        }
        fab.setImageResource(R.drawable.ic_add);
    }

    private boolean saveFormToElementModel(IElement el) {
        Log.e("EVideophoneActivity", "el=" + el);
        if (el == null) {
            return false;
        }
        if (!(this.inputName.getText().toString() == null || this.inputName.getText().toString().isEmpty())) {
            el.setName(this.inputName.getText().toString());
            if (!(this.inputAddress.getText().toString() == null || this.inputAddress.getText().toString().isEmpty())) {
                ((VideophoneIP) el).setAddress(this.inputAddress.getText().toString().replaceAll("\\s", ""));
                if (!(this.inputSipProxy.getText().toString() == null || this.inputSipProxy.getText().toString().isEmpty())) {
                    ((VideophoneIP) el).setSipProxy(this.inputSipProxy.getText().toString().replaceAll("\\s", ""));
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
