package ru.familion.lib_weather;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * https://tech.yandex.ru/weather/doc/dg/concepts/about-docpage/
 * 50 free calls per day
 */

/**
 * Weather module for https://tech.yandex.ru/weather/
 */
public class WeatherModuleYandex extends WeatherModule {

    private final static String PROVIDER = "yandex.ru";

    private final static String API_URL = "https://api.weather.yandex.ru/v1/informers?lat={lat}&lon={lon}";

    private String mApiKey;
    private String mApiUrl;

    public WeatherModuleYandex(Weather weather) {
        mProvider = PROVIDER;
        mWeather = weather;
        mApiKey = getStringResorceByName("yandex_weather_api_key");
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
        if (args.get("api_key")!= null) mApiKey = args.get("api_key");
        init();
        return this;
    }

    @Override
    public void getWeatherInfo(MapPoint point) {
        final Map<String, String> params = new HashMap<>();
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
                                mWeatherInfo = new WeatherInfo(this);
                                mWeatherInfo.setProvider(mProvider);
                                mWeatherInfo.setTemperature((float)(json.getJSONObject("main").getDouble("temp")));
                                mWeatherInfo.setPressure((float)(json.getJSONObject("main").getDouble("pressure")));
                                mWeatherInfo.setHumidity((float)(json.getJSONObject("main").getDouble("humidity")));
                                mWeatherInfo.setWindSpeed((float)(json.getJSONObject("wind").getDouble("speed")));
                                mWeatherInfo.isSuccess = true;
                            }
                            onWeatherInfoReady();
                        } catch (Exception e) {
                            mWeatherInfo.isSuccess = false;
                            e.printStackTrace();
                        }
                    }
                }, // Response.Listener
                // endregion
                // region new Response.ErrorListener(..)
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getClass().getName(), "call stringRequest.onErrorResponse: "+error.getMessage());
                        mWeatherInfo.isSuccess = false;
                    }
                } // Response.ErrorListener
                // endregion
        ){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Yandex-API-Key", mApiKey);
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Response<String> res = super.parseNetworkResponse(response);
                Map<String, String> headers = response.headers;
                return res;
            }
        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    @Override
    public void onWeatherInfoReady() {
        mWeather.onWeatherInfoReady(mWeatherInfo);
    }

}
