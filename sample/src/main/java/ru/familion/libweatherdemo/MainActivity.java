package ru.familion.libweatherdemo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.familion.lib_weather.Weather;
import ru.familion.lib_weather.WeatherInfo;
import ru.familion.lib_weather.WeatherListener;

import android.databinding.DataBindingUtil;
import android.widget.Toast;

import ru.familion.libweatherdemo.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private TextView txtProviderValue;
    private TextView txtTemperatureValue;
    private TextView txtPressureValue;
    private TextView txtHumidityValue;
    private TextView txtWindSpeedValue;
    private TextView txtLocationValue;

    private Weather weather;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setContainer(this);

        latitude = 49.448161;
        longitude = 11.075577;

        txtProviderValue = findViewById(R.id.txtProviderValue);
        txtTemperatureValue = findViewById(R.id.txtTemperatureValue);
        txtPressureValue = findViewById(R.id.txtPressureValue);
        txtHumidityValue = findViewById(R.id.txtHumidityValue);
        txtWindSpeedValue = findViewById(R.id.txtWindSpeedValue);
        txtLocationValue = findViewById(R.id.txtLocationValue);

        txtLocationValue.setText(latitude + " , " + longitude);


        weather = new Weather(this);
        String cfg = "{ " +
            "'weatherunlocked.com':{'disabled':false, 'app_id':'c5ef6284', 'api_key':'3a91d6f3f111aec28d6e1c169b5e31da'}, " +
            "'openweathermap.org':{'disabled':false} " +
        "}";
        try {
            weather.setConfig(new JSONObject(cfg));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        weather.addWeatherListener(new WeatherListener() {
            @Override
            public void onWeatherReady(WeatherInfo e) {
                txtProviderValue.setText(e.getProvider());
                txtTemperatureValue.setText(String.format("%.1f", e.getTemperature()));
                txtPressureValue.setText(String.format("%.0f", e.getPressure()));
                txtHumidityValue.setText(String.format("%.0f", e.getHumidity()));
                txtWindSpeedValue.setText(String.format("%.1f", e.getWindSpeed()));
            }
        });
        weather.getWeatherInfo(latitude, longitude);

    }

    public void onApixuButtonClick(View v) {
        Log.d("test", "onApixuButtonClick");
        weather.getWeatherInfo(Weather.APIXU, latitude, longitude);
    }

    public void onDarkskyButtonClick(View v) {
        Log.d("test", "onDarkskyButtonClick");
        weather.getWeatherInfo(Weather.DARKSKY, latitude, longitude);
    }

    public void onOpenweathermapButtonClick(View v) {
        Log.d("test", "onOpenweathermapButtonClick");
        weather.getWeatherInfo(Weather.OPENWEATHERMAP, latitude, longitude);
    }

    public void onWeatherunlockedButtonClick(View v) {
        Log.d("test", "onWeatherunlockedButtonClick");
        weather.getWeatherInfo(Weather.WEATHERUNLOCKED, latitude, longitude);
    }

    public void onYahooButtonClick(View v) {
        Log.d("test", "onYahooButtonClick");
        weather.getWeatherInfo(Weather.YAHOO, latitude, longitude);
    }

    public void onYandexButtonClick(View v) {
        Log.d("test", "onYandexButtonClick");
        weather.getWeatherInfo(Weather.YANDEX, latitude, longitude);
        Toast.makeText(this, getString(R.string.yandex_error), Toast.LENGTH_SHORT).show();
    }

    public void openMapForLocation(View v) {
        String coordinates = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(coordinates) );
        startActivity(intent);
    }

}
