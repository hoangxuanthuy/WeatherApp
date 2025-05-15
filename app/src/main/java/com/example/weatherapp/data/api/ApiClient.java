package com.example.weatherapp.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofitWeather;
    private static Retrofit retrofitIQAir;

    private static final String BASE_URL_WEATHER = "https://api.openweathermap.org/";
    private static final String BASE_URL_IQAIR = "https://api.airvisual.com/v2/";

    // Client cho OpenWeatherMap
    public static Retrofit getClient() {
        if (retrofitWeather == null) {
            retrofitWeather = new Retrofit.Builder()
                    .baseUrl(BASE_URL_WEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWeather;
    }

    // Client cho IQAir API
    public static Retrofit getClientIQAir() {
        if (retrofitIQAir == null) {
            retrofitIQAir = new Retrofit.Builder()
                    .baseUrl(BASE_URL_IQAIR)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitIQAir;
    }
}
