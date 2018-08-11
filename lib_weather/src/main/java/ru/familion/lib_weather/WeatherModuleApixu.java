package ru.familion.lib_weather;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * https://www.apixu.com/my/
 * 10,000 free calls per month (~320 per day)
 */

/**
 * Weather module for apixu.com
 */
public class WeatherModuleApixu extends WeatherModule {

    private final static String PROVIDER = "apixu.com";
    // multiplier for pressure transfer in mm Hg
    private final static double PRESS_MULT = 0.75006375541921;

    private final static String API_URL = "https://api.apixu.com/v1/current.json?key={api_key}&q={lat},{lon}";

    private String mApiKey;
    private String mApiUrl;

    public WeatherModuleApixu(Weather weather) {
        mProvider = PROVIDER;
        mWeather = weather;
        mApiKey = getStringResorceByName("apixu_weather_api_key");
        init();
    }

    /**
     * Init request url
     */
    private void init() {
        mApiUrl = API_URL.replace("{api_key}", mApiKey);
    }

    /**
     * Update service keys
     * @param args account access keys
     */
    @Override
    public WeatherModule setRequestArgs(HashMap<String,String> args) {
        if (args.get("api_key")!= null) mApiKey = args.get("api_key");
        init();
        return this;
    }

    @Override
    public void getWeatherInfo(MapPoint point) {
        mWeatherInfo = new WeatherInfo(this);
        mWeatherInfo.setProvider(mProvider);
        String url = mApiUrl.replace("{lat}", "" + point.getLatitude());
        url = url.replace("{lon}", "" + point.getLongitude());
        RequestQueue queue = Volley.newRequestQueue(mWeather.getCtx());
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                // region new Response.Listener<String>(..)
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(getClass().getName(), "call stringRequest.onResponse: " + response);
                        JSONObject json;
                        try {
                            json = new JSONObject(response);
                            JSONObject data = json.getJSONObject("current");
                            if (data != null) {
                                mWeatherInfo.setTemperature((float)data.getDouble("temp_c"));
                                mWeatherInfo.setPressure((float)(data.getDouble("pressure_mb")*PRESS_MULT));
                                mWeatherInfo.setHumidity((float)data.getDouble("humidity"));
                                mWeatherInfo.setWindSpeed((float)(data.getDouble("wind_kph")/3.6));
                                mWeatherInfo.isSuccess = true;
                            }
                        } catch (Exception e) {
                            mWeatherInfo.isSuccess = false;
                            e.printStackTrace();
                        }
                        onWeatherInfoReady();
                    }
                }, // Response.Listener
                // endregion
                // region new Response.ErrorListener(..)
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getClass().getName(), "call stringRequest.onErrorResponse: "+error.getMessage());
                        mWeatherInfo.isSuccess = false;
                        onWeatherInfoReady();
                    }
                } // Response.ErrorListener
                // endregion
        );
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    @Override
    public void onWeatherInfoReady() {
        mWeather.onWeatherInfoReady(mWeatherInfo);
    }

}
