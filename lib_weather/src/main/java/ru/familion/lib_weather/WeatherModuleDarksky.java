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
 * https://darksky.net/dev/account
 *  1,000 free calls per day
 */

/**
 * Weather module for darksky.net
 */
public class WeatherModuleDarksky extends WeatherModule {

    private final static String PROVIDER = "darksky.net";
    // multiplier for pressure transfer in mm Hg
    private final static double PRESS_MULT = 0.75006375541921;

    private final static String API_URL = "https://api.darksky.net/forecast/{api_key}/{lat},{lon}?units=si";

    private String mApiKey;
    private String mApiUrl;

    public WeatherModuleDarksky(Weather weather) {
        mProvider = PROVIDER;
        mWeather = weather;
        mApiKey = getStringResorceByName("darksky_weather_api_key");
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
                            JSONObject data = json.getJSONObject("currently");
                            if (data != null) {
                                mWeatherInfo.setTemperature((float)data.getDouble("temperature"));
                                mWeatherInfo.setPressure((float)(data.getDouble("pressure")*PRESS_MULT));
                                mWeatherInfo.setHumidity((float)data.getDouble("humidity")*100);
                                mWeatherInfo.setWindSpeed((float)data.getDouble("windSpeed"));
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
        queue.add(stringRequest);
    }

    @Override
    public void onWeatherInfoReady() {
        mWeather.onWeatherInfoReady(mWeatherInfo);
    }

}
