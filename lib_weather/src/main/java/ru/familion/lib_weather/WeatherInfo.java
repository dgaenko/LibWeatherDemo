package ru.familion.lib_weather;

import java.util.EventObject;

public class WeatherInfo extends EventObject {

    /**
     * Request result state
     */
    public boolean isSuccess;

    /**
     * Error message if request was failed
     */
    private String message;
    /**
     * Weather provider name (example: openweathermap.org)
     */
    private String mProvider;
    /**
     * Current temperature value (°C, example: 28)
     */
    private float mTemperature;
    /**
     * Current pressure value (mm Hg)
     */
    private float mPressure;
    /**
     * Current humidity value (in procent, example: 47)
     */
    private float mHumidity;
    /**
     * Current wind speed value (in m/s, example: 6.7)
     */
    private float mWindSpeed;

    /**
     * Constructor
     * @param source
     */
    public WeatherInfo(Object source) {
        super(source);
    }

    /**
     * Constructor
     * @param source
     * @param message
     */
    public WeatherInfo(Object source, String message) {
        super(source);
        this.message = message;
    }

    /**
     * Constructor
     * @param message
     */
    public WeatherInfo(String message){
        this(null, message);
    }

    @Override
    public String toString(){
        return getClass().getName() + "[source = " + getSource() + ", message = " + message + "]";
    }

    /**
     * Return provider name
     * @return
     */
    public String getProvider() {
        return mProvider;
    }

    /**
     * Set provider name
     * @param mProvider provider name
     */
    public void setProvider(String mProvider) {
        this.mProvider = mProvider;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float mTemperature) {
        this.mTemperature = mTemperature;
    }

    public float getPressure() {
        return mPressure;
    }

    public void setPressure(float mPressure) {
        this.mPressure = mPressure;
    }

    public float getHumidity() {
        return mHumidity;
    }

    public void setHumidity(float mHumidity) {
        this.mHumidity = mHumidity;
    }

    public float getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(float mWindSpeed) {
        this.mWindSpeed = mWindSpeed;
    }

}
