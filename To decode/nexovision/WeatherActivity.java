package eu.nexwell.android.nexovision;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter.Builder;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import eu.nexwell.android.nexovision.model.Weather;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import nexovision.android.nexwell.eu.nexovision.R;

public class WeatherActivity extends AppCompatActivity {
    private static Context context;
    private static FloatingActionButton fab;
    private static Handler handler;
    private static String weatherCity;
    private static String weatherCountryCode;

    /* renamed from: eu.nexwell.android.nexovision.WeatherActivity$1 */
    class C21001 implements PlaceSelectionListener {
        C21001() {
        }

        public void onPlaceSelected(Place place) {
            WeatherActivity.weatherCity = place.getName().toString();
            if (place.getLocale() != null) {
                WeatherActivity.weatherCountryCode = place.getLocale().getCountry().toLowerCase();
                return;
            }
            LatLng coordinates = place.getLatLng();
            List<Address> addresses = null;
            try {
                addresses = new Geocoder(WeatherActivity.getContext(), Locale.getDefault()).getFromLocation(coordinates.latitude, coordinates.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            WeatherActivity.weatherCountryCode = ((Address) addresses.get(0)).getCountryCode().toLowerCase();
        }

        public void onError(Status status) {
        }
    }

    /* renamed from: eu.nexwell.android.nexovision.WeatherActivity$2 */
    class C21012 implements OnClickListener {
        C21012() {
        }

        public void onClick(View view) {
            if (WeatherActivity.this.save()) {
                Snackbar.make(view, WeatherActivity.getContext().getString(R.string.WeatherActivity_SaveOKMessage), 0).setAction("Action", null).show();
                Weather.update();
                WeatherActivity.this.finish();
                return;
            }
            Snackbar.make(view, WeatherActivity.getContext().getString(R.string.WeatherActivity_SaveErrorMessage), 0).setAction("Action", null).show();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        handler = new Handler();
        setContentView(R.layout.activity_weather);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        String[] weatherLoc = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_weathercity", null).split(",");
        if (weatherLoc != null) {
            if (weatherLoc.length > 0) {
                weatherCity = weatherLoc[0];
            }
            if (weatherLoc.length > 1) {
                weatherCountryCode = weatherLoc[1];
            }
        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(new Builder().setTypeFilter(5).build());
        autocompleteFragment.setHint(getResources().getString(R.string.WeatherActivity_CityNameLabel));
        autocompleteFragment.setOnPlaceSelectedListener(new C21001());
        if (!(weatherCity == null || weatherCity.isEmpty())) {
            autocompleteFragment.setText(weatherCity);
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new C21012());
        }
    }

    private boolean save() {
        if (!(weatherCity == null || weatherCity.isEmpty())) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("pref_weathercity", weatherCity + "," + weatherCountryCode).commit();
        }
        return true;
    }

    public static Context getContext() {
        return context;
    }
}
