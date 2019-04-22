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
import android.widget.EditText;
import android.widget.SeekBar;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.NVModel;
import eu.nexwell.android.nexovision.model.Polygon;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorPolygonActivity extends AppCompatActivity {
    private static IElement TEMP_ELEMENT;
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private EditText inputName;
    private SeekBar sliderAlpha;

    /* renamed from: eu.nexwell.android.nexovision.EditorPolygonActivity$1 */
    class C19201 implements OnClickListener {
        C19201() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT != null) {
                if (EditorPolygonActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                    Snackbar.make(MainActivity.fragment, EditorPolygonActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                    EditorPolygonActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorPolygonActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorPolygonActivity.this.saveFormToElementModel(EditorPolygonActivity.TEMP_ELEMENT)) {
                NVModel.addElement(EditorPolygonActivity.TEMP_ELEMENT);
                NVModel.getCategory(NVModel.getElementTypeDefaultCategory(NVModel.CURR_ELEMENT.getType())).addElement(EditorPolygonActivity.TEMP_ELEMENT);
                Snackbar.make(view, EditorPolygonActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                EditorPolygonActivity.this.finish();
            } else {
                Snackbar.make(view, EditorPolygonActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_polygon);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.sliderAlpha = (SeekBar) findViewById(R.id.slider_alpha);
        this.sliderAlpha.setMax(100);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19201());
        }
        if (NVModel.CURR_ELEMENT != null) {
            TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorPolygonActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            if (NVModel.CURR_ELEMENT.getName() != null) {
                this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            }
            int alpha = ((Polygon) NVModel.CURR_ELEMENT).getAlpha();
            if (alpha < 0) {
                alpha = 0;
            }
            if (alpha > 100) {
                alpha = 100;
            }
            this.sliderAlpha.setProgress(alpha);
            fab.setImageResource(R.drawable.ic_save);
            return;
        }
        this.sliderAlpha.setProgress(50);
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
        ((Polygon) element).setAlpha(this.sliderAlpha.getProgress());
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
