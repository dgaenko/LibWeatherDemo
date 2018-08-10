package ru.familion.lib_weather;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.HashMap;


/**
 * https://developer.weatherunlocked.com/admin
 * Calls per Minute <75 & pre day <25,000
 * https://developer.weatherunlocked.com/buyer/stats
 */

/**
 * Weather module for weatherunlocked.com
 */
public class WeatherModuleWeatherunlocked extends WeatherModule {

    public final static String PROVIDER = "weatherunlocked.com";

    // multiplier for pressure transfer in mm Hg
    private final static double PRESS_MULT = 0.75006375541921;

    private final static String API_URL = "http://api.weatherunlocked.com/api/current/{lat},{lon}?app_id={app_id}&app_key={api_key}";

    private String mApiKey;
    private String mAppId;
    private String mApiUrl;

    public WeatherModuleWeatherunlocked(Weather weather) {
        mProvider = PROVIDER;
        mWeather = weather;
        mAppId = getStringResorceByName("weatherunlocked_weather_app_id");
        mApiKey = getStringResorceByName("weatherunlocked_weather_api_key");
        init();
    }

    /**
     * Init request url
     */
    private void init() {
        mApiUrl = API_URL.replace("{app_id}", mAppId);
        mApiUrl = mApiUrl.replace("{api_key}", mApiKey);
    }

    /**
     * Update service keys
     * @param args account access keys
     */
    @Override
    public WeatherModule setRequestArgs(HashMap<String,String> args) {
        if (args.get("app_id")!= null) mAppId = args.get("app_id");
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
                            //json = parseXmlUsePullParser(response);
                            json = new JSONObject(response);
                            if (json != null) {
                                mWeatherInfo.setTemperature((float)json.getDouble("temp_c"));
                                mWeatherInfo.setPressure((float)(json.getDouble("slp_mb")*PRESS_MULT));
                                mWeatherInfo.setHumidity((float)json.getDouble("humid_pct"));
                                mWeatherInfo.setWindSpeed((float)json.getDouble("windspd_ms"));
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

    // Provider Xml response parser
    private JSONObject parseXmlUsePullParser(String xmlString) {
        JSONObject json = new JSONObject();

        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = parserFactory.newPullParser();
            StringReader xmlStringReader = new StringReader(xmlString);
            xmlPullParser.setInput(xmlStringReader);
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                if (!TextUtils.isEmpty(nodeName)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        Log.d(getClass().getName(), "Start element " + nodeName);
                        String value = xmlPullParser.nextText();
                        json.put(nodeName, value);
                    } else if (eventType == XmlPullParser.END_TAG) {
                        Log.d(getClass().getName(), "End element " + nodeName);
                    }
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException ex) {
            json.put("error", ex.getMessage());
        } finally {
            return json;
        }
    }

}
