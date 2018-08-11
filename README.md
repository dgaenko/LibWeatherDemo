# LibWeather
Библиотека для получения погодных данных (температура, давление, влажность, скорость ветра) через публичные API провайдеров. Поддерживаются следующие провайдеры: [apixu.com](http://apixu.com), [darksky.net](http://darksky.net), [openweathermap.org](http://openweathermap.org), [weatherunlocked.com](http://weatherunlocked.com), [yahoo.com](https://developer.yahoo.com/weather/), [yandex.ru](https://yandex.ru/pogoda/).

## Лимиты и ограничения

Сервис-провайдеры предоставляют бесплатно некоторое количество запросов. При превышении указанных лимитов, доступ в API провайдера Вам, скорее всего, заблокируют и предложат перейти на один из платных пакетов. 

**Ограничения на количество запросов (данные на август 2018 г)**

| apixu.com | darksky.net | openweathermap.org | weatherunlocked.com | yahoo.com | yandex.ru |
|:---------------:|:---------:|:----------------:|:----------------:|:----------------:|:----------------:|
| <10 000 в месяц | <1000 в день | <60 в мин | <75 в мин, <25 000 в день | <2000 в день | <50 в день |

## Использование

+ Импортируйте библиотеку как модуль в Ваш проект (File -> New -> Import module...)
+ В файле ресурсов **strings.xml** добавьте следующие строки с ключами доступа к API провайдеров

```xml
    <string name="weatherunlocked_weather_app_id">xxxxxxxx</string>
    <string name="weatherunlocked_weather_api_key">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
    <string name="openweathermap_api_key">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
    <string name="darksky_weather_api_key">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
    <string name="apixu_weather_api_key">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
    <string name="yandex_weather_api_key">xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx</string>
```
+ В приложении для получения данных используйте следующий код:
```java
...
import ru.familion.lib_weather.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Weather weather = new Weather(this);
        weather.addWeatherListener(new WeatherListener() {
            @Override
            public void onWeatherReady(WeatherInfo e) {
                Log.d("APP", "Temperature: " + e.getTemperature() + "°C");
                Log.d("APP", "Pressure: " + e.getPressure() + "mm Hg");
                Log.d("APP", "Humidity: " + e.getHumidity() + "%");
                Log.d("APP", "Wind speed: " + e.getWindSpeed() + "m/s");
                Log.d("APP", "Status: " + e.isSuccess ? "OK" : "Error");
            }
        });
        // получение инфо о погоде в указанном координатами месте
        weather.getWeatherInfo(49.448161, 11.075577);
        
    }
}
```
Варианты использования:
```java
        weather.getWeatherInfo(49.448161, 11.075577);
```
Получает информацию о погоде от провайдеров без конкретизации сервиса. При ошибке получения данных будет производится перебор провадеров с следующем порядке: weatherunlocked.com, openweathermap.org, yahoo.com, darksky.net, apixu.com, yandex.ru.
```java
        int module_id = Weather.APIXU;
        weather.getWeatherInfo(module_id, 49.448161, 11.075577);
```
При ошибке получения данных поле WeatherInfo.isSuccess будет иметь значение false.

Запрос информации о погоде будет произведен от указанного в module_id сервис-провайдера. Список значений для выбора провайдера данных:
```java
Weather.APIXU
Weather.DARKSKY
Weather.OPENWEATHERMAP
Weather.WEATHERUNLOCKED
Weather.YAHOO
Weather.YANDEX
```

<img src="https://github.com/dgaenko/LibWeatherDemo/blob/master/sample/screenshot/main.png" width="350"/>
