package ru.familion.lib_weather;

import java.util.HashMap;

/**
 * Weather module interface
 */
public interface WeatherModuleInterface {

    /**
     * Set weather configation
     * @param args Map with weather providers access keys
     * @return weather module instance
     */
    public WeatherModule setRequestArgs(HashMap<String,String> args);

    /**
     * Start request weather from providers
     * @param point location for request
     */
    public void getWeatherInfo(MapPoint point);

    /**
     * Ready data providers handler
     */
    public void onWeatherInfoReady();

}
