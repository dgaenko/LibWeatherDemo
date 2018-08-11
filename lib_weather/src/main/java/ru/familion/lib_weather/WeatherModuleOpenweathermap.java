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
 * https://home.openweathermap.org/api_keys
 * Calls per minute < 60
 */

/**
 * Weather module for openweathermap.org
 */
public class WeatherModuleOpenweathermap extends WeatherModule {

    private final static String PROVIDER = "openweathermap.org";
    // multiplier for pressure transfer in mm Hg
    private final static double PRESS_MULT = 0.75006375541921;

    private final static String API_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&APPID={api_key}&lat={lat}&lon={lon}";

    private String mApiKey;
    private String mApiUrl;

    /**
     * Constructor
     * @param weather
     */
    public WeatherModuleOpenweathermap(Weather weather) {
        mProvider = PROVIDER;
        mWeather = weather;
        mApiKey = getStringResorceByName("openweathermap_api_key");
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
                            if (json.optInt("cod")==200) {
                                mWeatherInfo.setTemperature((float)(json.getJSONObject("main").getDouble("temp")));
                                mWeatherInfo.setPressure((float)(json.getJSONObject("main").getDouble("pressure")*PRESS_MULT));
                                mWeatherInfo.setHumidity((float)(json.getJSONObject("main").getDouble("humidity")));
                                mWeatherInfo.setWindSpeed((float)(json.getJSONObject("wind").getDouble("speed")));
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
