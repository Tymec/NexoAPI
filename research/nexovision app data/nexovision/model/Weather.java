package eu.nexwell.android.nexovision.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.search.SearchAuth.StatusCodes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.nexwell.android.nexovision.MainActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class Weather {
    private static String LOG_TAG = "Weather";
    private static String appId = "dfcb362ad7daf6ee944136f4f47309ca";
    private static String baseUrl = ("http://api.openweathermap.org/data/2.5/weather?units=metric&appid=" + appId + "&q=");
    private static Drawable icon = null;
    private static String iconUrl = "http://openweathermap.org/img/w/";
    private static String[] icons;
    private static String lastInfo = null;
    private static ArrayList<WeatherListener> listeners;

    public static class DoRead extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... _url) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            HttpConnectionParams.setSoTimeout(httpParams, StatusCodes.AUTH_DISABLED);
            try {
                HttpResponse res = httpclient.execute(new HttpGet(URI.create(_url[0].replaceAll("\\s", ""))));
                if (res.getStatusLine().getStatusCode() == 401) {
                    Log.e(Weather.LOG_TAG, "ERR 401");
                    return null;
                }
                String info;
                JsonObject jsonObject;
                String city;
                JsonObject jsonMain;
                String icon_id;
                Float temp;
                String pressure;
                String humidity;
                String windSpeed;
                String finalInfo;
                String[] arrayInfo;
                Log.e(Weather.LOG_TAG, "res=" + res.getStatusLine().getStatusCode());
                StringBuffer buffer = new StringBuffer();
                InputStream is = res.getEntity().getContent();
                if (is != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    Log.d(Weather.LOG_TAG, "Start getting data from server...");
                    while (true) {
                        String line = br.readLine();
                        if (line != null) {
                            Log.d(Weather.LOG_TAG, "Line: " + line);
                            buffer.append(line + "\r\n");
                        }
                    }
                    info = buffer.toString();
                    jsonObject = new JsonParser().parse(info).getAsJsonObject();
                    city = jsonObject.get("name").getAsString();
                    jsonMain = jsonObject.get("main").getAsJsonObject();
                    icon_id = jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                    temp = Float.valueOf(jsonMain.get("temp").getAsFloat());
                    pressure = jsonMain.get("pressure").getAsString();
                    humidity = jsonMain.get("humidity").getAsString();
                    windSpeed = jsonObject.get("wind").getAsJsonObject().get("speed").getAsString();
                    finalInfo = "City: " + city + "\nTemp: " + String.format("%.1f", new Object[]{temp}) + " °C\nPressure: " + pressure + " hPa\nHumidity: " + humidity + " %\nWind: " + windSpeed + " m/s";
                    arrayInfo = new String[5];
                    arrayInfo[1] = String.format("%.1f", new Object[]{temp});
                    arrayInfo[2] = windSpeed;
                    arrayInfo[3] = humidity;
                    arrayInfo[4] = pressure;
                    if (icon_id != null) {
                        Weather.icon = Weather.getDrawable("ic_weather_" + icon_id);
                    }
                    Weather.notifyListeners(arrayInfo, Weather.icon);
                    return info;
                }
                info = buffer.toString();
                jsonObject = new JsonParser().parse(info).getAsJsonObject();
                city = jsonObject.get("name").getAsString();
                jsonMain = jsonObject.get("main").getAsJsonObject();
                icon_id = jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                temp = Float.valueOf(jsonMain.get("temp").getAsFloat());
                pressure = jsonMain.get("pressure").getAsString();
                humidity = jsonMain.get("humidity").getAsString();
                windSpeed = jsonObject.get("wind").getAsJsonObject().get("speed").getAsString();
                finalInfo = "City: " + city + "\nTemp: " + String.format("%.1f", new Object[]{temp}) + " °C\nPressure: " + pressure + " hPa\nHumidity: " + humidity + " %\nWind: " + windSpeed + " m/s";
                arrayInfo = new String[5];
                arrayInfo[1] = String.format("%.1f", new Object[]{temp});
                arrayInfo[2] = windSpeed;
                arrayInfo[3] = humidity;
                arrayInfo[4] = pressure;
                if (icon_id != null) {
                    Weather.icon = Weather.getDrawable("ic_weather_" + icon_id);
                }
                Weather.notifyListeners(arrayInfo, Weather.icon);
                return info;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e(Weather.LOG_TAG, "Request failed-ClientProtocolException", e);
                return null;
            } catch (IOException e2) {
                e2.printStackTrace();
                Log.e(Weather.LOG_TAG, "Request failed-IOException", e2);
                return null;
            }
        }

        protected void onPostExecute(String result) {
            Log.d(Weather.LOG_TAG, "RESULT: " + result);
        }
    }

    public interface WeatherListener {
        void updateWeatherInfo(String[] strArr, Drawable drawable);
    }

    static {
        listeners = null;
        listeners = new ArrayList();
    }

    public static void addWeatherListener(WeatherListener l) {
        listeners.add(l);
    }

    public static void removeWeatherListener(WeatherListener l) {
        listeners.remove(l);
    }

    public static void update() {
        new DoRead().execute(new String[]{baseUrl + PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext()).getString("pref_weathercity", "Wroclaw,pl")});
    }

    private static void notifyListeners(String[] info, Drawable icon) {
        Iterator<WeatherListener> itrl = listeners.iterator();
        while (itrl.hasNext()) {
            ((WeatherListener) itrl.next()).updateWeatherInfo(info, icon);
        }
    }

    public static Drawable getDrawable(String name) {
        Context context = MainActivity.getContext();
        int resourceId = context.getResources().getIdentifier(name, "drawable", MainActivity.getContext().getPackageName());
        if (resourceId == 0) {
            return null;
        }
        return context.getResources().getDrawable(resourceId);
    }
}
