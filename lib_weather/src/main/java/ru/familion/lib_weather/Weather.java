package ru.familion.lib_weather;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Weather class
 */
public class Weather {

    public final static int WEATHERUNLOCKED = 0;
    public final static int OPENWEATHERMAP  = 1;
    public final static int YAHOO           = 2;
    public final static int DARKSKY         = 3;
    public final static int APIXU           = 4;
    public final static int YANDEX          = 5;

    private Context mCtx;
    /**
     * weather modules list
     */
    private ArrayList<WeatherModule> modules = new ArrayList<WeatherModule>();
    /**
     * weather listeners
     */
    private ArrayList<WeatherListener> listeners = new ArrayList<WeatherListener>();
    /**
     * Last used module for weather request
     */
    private int lastUsedModule = 0;
    /**
     * Weather point
     */
    private MapPoint mapPoint;

    public Context getCtx() {
        return mCtx;
    }

    /**
     * Constructor
     * @param ctx
     */
    public Weather(Context ctx) {
        mCtx = ctx;
        modules.add(new WeatherModuleWeatherunlocked(this));
        modules.add(new WeatherModuleOpenweathermap(this));
        modules.add(new WeatherModuleYahoo(this));
        modules.add(new WeatherModuleDarksky(this));
        modules.add(new WeatherModuleApixu(this));
        // yandex don't works
        //modules.add(new WeatherModuleYandex(this));
    }

    /**
     * Set weather configuration for modules
     * @param cfg JSONObject with configuration for weather library
     * example:
     * String str = '{
     *     "weatherunlocked.com": {
     *         "disabled": true,
     *         "app_id": "XXXXXXXXX"
     *         "api_key": "XXXXXXXXX"
     *     },
     *     "openweathermap.org": {
     *         "disabled": true
     *     }
     * }';
     * JSONObject = new JSONObject(str);
     */
    public void setConfig(JSONObject cfg) {
        if (cfg != null) {
            for (WeatherModule module: modules) {
                JSONObject item = cfg.optJSONObject(module.getProvider());
                if (item != null) {
                    module.setEnabled(!item.optBoolean("disabled", false));
                    module.setRequestArgs(makeArgsForModule(item));
                }
            }
        }
    }

    /**
     * Make HashMap arguments for providers url
     * @param cfg
     * @return
     */
    private HashMap<String, String> makeArgsForModule(JSONObject cfg) {
        HashMap<String,String> args = new HashMap<String,String>();
        Iterator<?> keys = cfg.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            try {
                if ( cfg.get(key) instanceof String ) {
                    args.put(key, cfg.optString(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return args;
    }

    /**
     * Request weather info from providers
     * @param latitude  Location latitude
     * @param longitude Location longitude
     */
    public void getWeatherInfo(double latitude, double longitude) {
        MapPoint point = new MapPoint(latitude, longitude);
        getWeatherInfo(point);
    }

    /**
     * Request weather info from specified provider module
     * @param moduleNumber required module
     * @param latitude  Location latitude
     * @param longitude Location longitude
     */
    public void getWeatherInfo(int moduleNumber, double latitude, double longitude) {
        MapPoint point = new MapPoint(latitude, longitude);
        getWeatherInfo(moduleNumber, point);
    }

    /**
     * Request weather info from providers
     * @param point point location for weather request
     */
    public void getWeatherInfo(MapPoint point) {
        getWeatherInfo(0, point);
    }

    /**
     * Request weather info from specified provider module
     * @param moduleNumber required module
     * @param point point location for weather request
     */
    public void getWeatherInfo(int moduleNumber, MapPoint point) {
        if (moduleNumber < modules.size()) {
            mapPoint = point;
            lastUsedModule = moduleNumber;
            WeatherModule weatherModule = modules.get(moduleNumber);
            if (weatherModule.isEnabled()) {
                weatherModule.getWeatherInfo(point);
            } else {
                getWeatherInfo(moduleNumber + 1, point);
            }
        } else {
            // weather request failed
            WeatherInfo weatherInfo = new WeatherInfo(this, "Error request weather");
            weatherInfo.isSuccess = true;
            onWeatherInfoReady(weatherInfo);
        }
    }

    /**
     * Called when weather info is ready
     * @param weatherInfo weather details
     */
    public void onWeatherInfoReady(WeatherInfo weatherInfo) {
        Log.d(getClass().getName(), "onWeatherInfoReady");
        if (weatherInfo.isSuccess) {
            if (weatherInfo.getProvider()==null) {
                weatherInfo.isSuccess = false;
            }
            for (WeatherListener listener: listeners) {
                listener.onWeatherReady(weatherInfo);
            }
        } else {
            getWeatherInfo(lastUsedModule + 1, mapPoint);
        }
    }

    /**
     * Add weather listener to array
     * @param listener
     */
    public void addWeatherListener(WeatherListener listener){
        listeners.add(listener);
    }

    /**
     * Remove weather listener to array
     * @param listener
     */
    public void removeWeatherListener(WeatherListener listener){
        listeners.remove(listener);
    }

    /**
     * Return listeners array
     * @return
     */
    public WeatherListener[] getWeatherListeners(){
        return listeners.toArray(new WeatherListener[listeners.size()]);
    }

}
