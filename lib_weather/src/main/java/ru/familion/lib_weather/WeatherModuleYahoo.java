package ru.familion.lib_weather;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;

/**
 * https://developer.yahoo.com/weather/#ratelimits
 * 2,000 free calls per day
 */

/**
 * Weather module for yahoo.com
 */
public class WeatherModuleYahoo extends WeatherModule {

    private final static String PROVIDER = "yahoo.com";
    // multiplier for pressure transfer in mm Hg
    private final static double MB_TO_MM_HG = 45.15186352;

    private final static String API_URL = "http://query.yahooapis.com/v1/public/yql?q={query}&format=json";
    private final static String API_QUERY = "select * from weather.forecast where u=\"c\" and woeid in (select woeid from geo.places(1) where text=\"({lat},{lon})\")";

    private String mApiKey;
    private String mApiUrl;

    public WeatherModuleYahoo(Weather weather) {
        mProvider = PROVIDER;
        mWeather = weather;
        //mApiKey = getStringResorceByName("yahoo_weather_api_key");
        init();
    }

    /**
     * Init request url
     */
    private void init() {
    }

    /**
     * Update service keys
     * @param args account access keys
     */
    @Override
    public WeatherModule setRequestArgs(HashMap<String,String> args) {
        init();
        return this;
    }

    @Override
    public void getWeatherInfo(MapPoint point) {
        mWeatherInfo = new WeatherInfo(this);
        mWeatherInfo.setProvider(mProvider);
        String s = API_QUERY.replace("{lat}", "" + point.getLatitude());
        s = s.replace("{lon}", "" + point.getLongitude());
        String url = API_URL.replace("{query}", URLEncoder.encode(s));
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
                            JSONObject data = json.getJSONObject("query");
                            if (data != null && data.optInt("count") > 0) {
                                data = data.getJSONObject("results").getJSONObject("channel");
                                mWeatherInfo.setTemperature((float)data.getJSONObject("item").getJSONObject("condition").getDouble("temp"));
                                mWeatherInfo.setPressure((float)(data.getJSONObject("atmosphere").getDouble("pressure")/MB_TO_MM_HG));
                                mWeatherInfo.setHumidity((float)data.getJSONObject("atmosphere").getDouble("humidity"));
                                mWeatherInfo.setWindSpeed((float)data.getJSONObject("wind").getDouble("speed")/3.6f);
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
