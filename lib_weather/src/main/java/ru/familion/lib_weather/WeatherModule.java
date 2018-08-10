package ru.familion.lib_weather;

import android.content.Context;

public abstract class WeatherModule implements WeatherModuleInterface {

    protected String mProvider = "WEATHER_MODULE";

    protected boolean mEnabled = true;
    protected Weather mWeather;
    protected WeatherInfo mWeatherInfo;


    public String getProvider() {
        return mProvider;
    }
    public boolean isEnabled() {
        return mEnabled;
    }
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    /**
     * Get string resource by name
     * @param resourceName
     * @return
     */
    public String getStringResorceByName(String resourceName) {
        Context ctx = mWeather.getCtx();
        int id = ctx.getResources().getIdentifier(resourceName, "string", ctx.getPackageName());
        return ctx.getResources().getString(id);
    }
}
