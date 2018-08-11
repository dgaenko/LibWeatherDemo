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

strings.example.xml

<img src="https://github.com/dgaenko/LibWeatherDemo/blob/master/sample/screenshot/main.png" width="350"/>
