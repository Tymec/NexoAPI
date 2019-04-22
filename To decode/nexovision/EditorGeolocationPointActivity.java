package eu.nexwell.android.nexovision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlacePicker$IntentBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import eu.nexwell.android.nexovision.model.GeolocationPoint;
import eu.nexwell.android.nexovision.model.IElement;
import eu.nexwell.android.nexovision.model.Logic;
import eu.nexwell.android.nexovision.model.NVModel;
import java.util.ArrayList;
import nexovision.android.nexwell.eu.nexovision.R;

public class EditorGeolocationPointActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static Context context;
    private static FloatingActionButton fab;
    private static FloatingActionButton fabMap;
    private static Handler handler;
    private int PLACE_PICKER_REQUEST = 1;
    private IElement TEMP_ELEMENT;
    private Button button_currloc;
    private Button button_maploc;
    private LinearLayout imageButtons;
    private ImageView imageWallpaper;
    private EditText inputEnterMessage;
    private EditText inputExitMessage;
    private EditText inputName;
    private EditText inputRadius;
    private Location location = null;
    private TextView location_info;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Place place = null;
    private ScrollView scrollView;
    private CustomSpinner spinnerEnterLogic;
    private CustomSpinner spinnerExitLogic;

    /* renamed from: eu.nexwell.android.nexovision.EditorGeolocationPointActivity$1 */
    class C19111 implements OnClickListener {
        C19111() {
        }

        public void onClick(View view) {
            if (NVModel.CURR_ELEMENT == null) {
                EditorGeolocationPointActivity.this.TEMP_ELEMENT = NVModel.newElement(NVModel.EL_TYPE_GEOLOCATIONPOINT);
                if (EditorGeolocationPointActivity.this.saveFormToElementModel(EditorGeolocationPointActivity.this.TEMP_ELEMENT)) {
                    NVModel.addElement(EditorGeolocationPointActivity.this.TEMP_ELEMENT);
                    NVModel.getCategory(NVModel.CATEGORY_GEOLOCATION).addElement(EditorGeolocationPointActivity.this.TEMP_ELEMENT);
                    Snackbar.make(view, EditorGeolocationPointActivity.getContext().getString(R.string.EditorActivity_AddOKMessage), 0).setAction("Action", null).show();
                    EditorGeolocationPointActivity.this.finish();
                    return;
                }
                Snackbar.make(view, EditorGeolocationPointActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            } else if (EditorGeolocationPointActivity.this.saveFormToElementModel(NVModel.CURR_ELEMENT)) {
                Snackbar.make(MainActivity.fragment, EditorGeolocationPointActivity.getContext().getString(R.string.EditorActivity_SaveOKMessage), 0).setAction("Action", null).show();
                EditorGeolocationPointActivity.this.finish();
            } else {
                Snackbar.make(view, EditorGeolocationPointActivity.getContext().getString(R.string.EditorActivity_FormErrMessage), 0).setAction("Action", null).show();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.EditorGeolocationPointActivity$2 */
    class C19122 implements OnClickListener {
        C19122() {
        }

        public void onClick(View view) {
            try {
                EditorGeolocationPointActivity.this.startActivityForResult(new PlacePicker$IntentBuilder().build((Activity) EditorGeolocationPointActivity.context), EditorGeolocationPointActivity.this.PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.EditorGeolocationPointActivity$4 */
    class C19144 implements Runnable {
        C19144() {
        }

        public void run() {
            EditorGeolocationPointActivity.this.scrollView.scrollTo(0, 0);
        }
    }

    private class ScrollPositionObserver implements OnScrollChangedListener {
        private int mImageViewHeight;

        public ScrollPositionObserver() {
            this.mImageViewHeight = EditorGeolocationPointActivity.this.getResources().getDimensionPixelSize(R.dimen.activity_editor_image_height);
        }

        public void onScrollChanged() {
            int scrollY = Math.min(Math.max(EditorGeolocationPointActivity.this.scrollView.getScrollY(), 0), this.mImageViewHeight);
            EditorGeolocationPointActivity.this.imageWallpaper.setTranslationY((float) (scrollY / 2));
            EditorGeolocationPointActivity.this.imageWallpaper.getLayoutParams().height = this.mImageViewHeight - (scrollY / 2);
            EditorGeolocationPointActivity.this.imageWallpaper.requestLayout();
            EditorGeolocationPointActivity.this.imageButtons.setTranslationY((float) (scrollY / 2));
            EditorGeolocationPointActivity.this.imageButtons.getLayoutParams().height = this.mImageViewHeight - (scrollY / 2);
            EditorGeolocationPointActivity.this.imageButtons.requestLayout();
            float alpha = ((float) scrollY) / ((float) this.mImageViewHeight);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_editor_geolocpoint);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.scrollView = (ScrollView) findViewById(R.id.scroll);
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        this.imageWallpaper = (ImageView) findViewById(R.id.image_wallpaper);
        this.imageButtons = (LinearLayout) findViewById(R.id.image_buttons);
        this.location_info = (TextView) findViewById(R.id.location_info);
        this.button_currloc = (Button) findViewById(R.id.button_currloc);
        this.button_maploc = (Button) findViewById(R.id.button_maploc);
        this.inputName = (EditText) findViewById(R.id.input_name);
        this.inputEnterMessage = (EditText) findViewById(R.id.input_enter_message);
        this.spinnerEnterLogic = (CustomSpinner) findViewById(R.id.spinner_enter_logic);
        this.inputExitMessage = (EditText) findViewById(R.id.input_exit_message);
        this.spinnerExitLogic = (CustomSpinner) findViewById(R.id.spinner_exit_logic);
        this.inputRadius = (EditText) findViewById(R.id.input_radius);
        this.mapFragment.getMapAsync(this);
        ArrayList<String> logics = NVModel.getElementNamesByType(NVModel.EL_TYPE_LOGIC);
        logics.add(0, getString(R.string.EditorActivity_Form_NoLogic));
        this.spinnerEnterLogic.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, logics));
        this.spinnerExitLogic.setAdapter(new ArrayAdapter(getContext(), R.layout.spinner_item, logics));
        this.location_info.setText("Location: unknown");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C19111());
        }
        fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        if (fabMap != null) {
            fabMap.setOnClickListener(new C19122());
        }
        if (NVModel.CURR_ELEMENT != null) {
            this.TEMP_ELEMENT = NVModel.newElement(NVModel.CURR_ELEMENT.getType());
            this.TEMP_ELEMENT.setId(NVModel.CURR_ELEMENT.getId().intValue());
            this.TEMP_ELEMENT.setName(NVModel.CURR_ELEMENT.getName());
            this.TEMP_ELEMENT.setBackgrounds(NVModel.CURR_ELEMENT.getBackgrounds());
            ((GeolocationPoint) this.TEMP_ELEMENT).setOnEnterMessage(((GeolocationPoint) NVModel.CURR_ELEMENT).getOnEnterMessage());
            ((GeolocationPoint) this.TEMP_ELEMENT).setOnExitMessage(((GeolocationPoint) NVModel.CURR_ELEMENT).getOnExitMessage());
            ((GeolocationPoint) this.TEMP_ELEMENT).setOnEnterLogic(((GeolocationPoint) NVModel.CURR_ELEMENT).getOnEnterLogic());
            ((GeolocationPoint) this.TEMP_ELEMENT).setOnExitLogic(((GeolocationPoint) NVModel.CURR_ELEMENT).getOnExitLogic());
            ((GeolocationPoint) this.TEMP_ELEMENT).setRadius(((GeolocationPoint) NVModel.CURR_ELEMENT).getRadius());
            ((GeolocationPoint) this.TEMP_ELEMENT).setLocation(((GeolocationPoint) NVModel.CURR_ELEMENT).getLocation());
            ((GeolocationPoint) this.TEMP_ELEMENT).setDistance(-1.0f);
            handler.post(new Runnable() {
                public void run() {
                    toolbar.setTitle(NVModel.getElementTypeName(EditorGeolocationPointActivity.getContext(), NVModel.CURR_ELEMENT.getType()));
                }
            });
            this.inputName.setText(NVModel.CURR_ELEMENT.getName());
            this.inputEnterMessage.setText(((GeolocationPoint) NVModel.CURR_ELEMENT).getOnEnterMessage());
            this.inputExitMessage.setText(((GeolocationPoint) NVModel.CURR_ELEMENT).getOnExitMessage());
            this.inputRadius.setText(((GeolocationPoint) NVModel.CURR_ELEMENT).getRadius() + "");
            Logic enter_logic = ((GeolocationPoint) this.TEMP_ELEMENT).getOnEnterLogic();
            if (enter_logic != null) {
                this.spinnerEnterLogic.setSelection(NVModel.getElementsByType(NVModel.EL_TYPE_LOGIC).indexOf(enter_logic) + 1);
            } else {
                this.spinnerEnterLogic.setSelection(0);
            }
            Logic exit_logic = ((GeolocationPoint) this.TEMP_ELEMENT).getOnExitLogic();
            if (exit_logic != null) {
                this.spinnerExitLogic.setSelection(NVModel.getElementsByType(NVModel.EL_TYPE_LOGIC).indexOf(exit_logic) + 1);
            } else {
                this.spinnerExitLogic.setSelection(0);
            }
            this.location = ((GeolocationPoint) this.TEMP_ELEMENT).getLocation();
            if (this.location != null) {
                this.location_info.setText("Latitude: " + this.location.getLatitude() + "\nLongitude: " + this.location.getLongitude() + "\nAccuracy: " + this.location.getAccuracy());
            } else {
                this.location_info.setText("Location: unknown");
            }
            fab.setImageResource(R.drawable.ic_save);
        } else {
            fab.setImageResource(R.drawable.ic_add);
        }
        this.place = null;
    }

    private boolean saveFormToElementModel(IElement el) {
        IElement element = el;
        if (this.inputName.getText().toString() == null || this.inputName.getText().toString().isEmpty() || this.inputRadius.getText().toString() == null || this.inputRadius.getText().toString().isEmpty() || this.location == null) {
            return false;
        }
        Logic logic;
        element.setName(this.inputName.getText().toString());
        if (el != this.TEMP_ELEMENT) {
            el.setBackgrounds(this.TEMP_ELEMENT.getBackgrounds());
        }
        if (this.spinnerEnterLogic.getSelectedItemPosition() <= 0) {
            ((GeolocationPoint) element).setOnEnterLogic((Integer) null);
        } else {
            logic = (Logic) NVModel.getElementsByType(NVModel.EL_TYPE_LOGIC).get(this.spinnerEnterLogic.getSelectedItemPosition() - 1);
            if (logic != null) {
                ((GeolocationPoint) element).setOnEnterLogic(logic);
            }
        }
        if (this.spinnerExitLogic.getSelectedItemPosition() <= 0) {
            ((GeolocationPoint) element).setOnExitLogic((Integer) null);
        } else {
            logic = (Logic) NVModel.getElementsByType(NVModel.EL_TYPE_LOGIC).get(this.spinnerExitLogic.getSelectedItemPosition() - 1);
            if (logic != null) {
                ((GeolocationPoint) element).setOnExitLogic(logic);
            }
        }
        if (!(this.inputEnterMessage.getText().toString() == null || this.inputEnterMessage.getText().toString().isEmpty())) {
            ((GeolocationPoint) element).setOnEnterMessage(this.inputEnterMessage.getText().toString());
        }
        if (!(this.inputExitMessage.getText().toString() == null || this.inputExitMessage.getText().toString().isEmpty())) {
            ((GeolocationPoint) element).setOnExitMessage(this.inputExitMessage.getText().toString());
        }
        if (!(this.inputRadius.getText().toString() == null || this.inputRadius.getText().toString().isEmpty())) {
            ((GeolocationPoint) element).setRadius(Integer.parseInt(this.inputRadius.getText().toString()));
        }
        if (this.location != null) {
            ((GeolocationPoint) element).setLocation(this.location);
        }
        return true;
    }

    public static Context getContext() {
        return context;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.PLACE_PICKER_REQUEST && resultCode == -1) {
            MainActivity.displayInfo((Context) this, String.format("Place: %s", new Object[]{PlacePicker.getPlace(data, this).getName()}));
            this.place = p;
            this.location = new Location("L");
            this.location.setLatitude(this.place.getLatLng().latitude);
            this.location.setLongitude(this.place.getLatLng().longitude);
            String name = "";
            String address = "";
            if (!(this.place.getName() == null || this.place.getName().toString().isEmpty())) {
                name = "Name: " + this.place.getName().toString() + "\n";
            }
            if (!(this.place.getAddress() == null || this.place.getAddress().toString().isEmpty())) {
                address = "Address: " + this.place.getAddress().toString() + "\n";
            }
            if (this.location != null) {
                this.location_info.setText(name + address + "Latitude: " + this.location.getLatitude() + "\nLongitude: " + this.location.getLongitude());
            } else {
                this.location_info.setText("Location: unknown");
            }
            this.mapFragment.getMapAsync(this);
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    protected void onResume() {
        getSupportActionBar().setTitle(NVModel.getElementTypeName(context, NVModel.EL_TYPE_GEOLOCATIONPOINT));
        handler.post(new C19144());
        super.onResume();
    }

    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        LatLng point = null;
        if (this.place != null) {
            point = this.place.getLatLng();
        } else if (this.TEMP_ELEMENT != null) {
            Location loc = ((GeolocationPoint) this.TEMP_ELEMENT).getLocation();
            point = new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        this.map.clear();
        if (point != null) {
            this.map.addMarker(new MarkerOptions().position(point).title("Selected position"));
            this.map.moveCamera(CameraUpdateFactory.newLatLng(point));
            this.map.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        } else {
            this.map.animateCamera(CameraUpdateFactory.zoomTo(0.0f));
        }
        this.map.setBuildingsEnabled(true);
        this.map.setIndoorEnabled(true);
        this.map.getUiSettings().setZoomControlsEnabled(true);
        this.map.getUiSettings().setAllGesturesEnabled(false);
        this.map.setMapType(4);
    }
}
