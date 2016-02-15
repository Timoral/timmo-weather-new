package xyz.timmo.weather;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class City {

    private static final String CLASS_NAME = "City";
    private static final String APP_ID = "&appid=efb7b9888b0708746bd71d6251ef709c";
    private static final String CURRENT_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String PREF_CITY_ID = "PREF_CITY_ID";

    public static String SetCity(Context context, String city) {
        String jsonStr = new JSONHandler().makeServiceCall(CURRENT_URL + city + APP_ID, JSONHandler.GET);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                if (jsonObj.getString("message").equals("Error: Not found city")) {
                    return null;
                }
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putString(PREF_CITY_ID, jsonObj.getString("id")).apply();
            } catch (JSONException e) {
                Log.e(CLASS_NAME, e.toString());
            }
        }
        return null;
    }


    public static String getCity(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_CITY_ID, "2643743");
    }

}
