package com.example.weatherapp.data.api;

import com.example.weatherapp.data.model.AirQualityResponse;
import com.example.weatherapp.data.model.CurrentWeather;
import com.example.weatherapp.data.model.ForecastResponse;
import com.example.weatherapp.data.model.GeocodingResponse;
import com.example.weatherapp.data.model.IQAirResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    // API 1: Current weather by city name
    @GET("data/2.5/weather")
    Call<CurrentWeather> getCurrentWeatherByCity(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    // API 2: Get city name from coordinates
    @GET("geo/1.0/reverse")
    Call<List<GeocodingResponse>> getCityNameFromCoordinates(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("lang") String lang
    );

    // API 4: Forecast
    @GET("data/2.5/forecast")
    Call<ForecastResponse> getForecast(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    // API 5: Air quality
    @GET("nearest_city")
    Call<IQAirResponse> getIQAirAirQuality(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("key") String apiKey
    );
    @GET("data/2.5/air_pollution")
    Call<AirQualityResponse> getAirPollutionData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey
    );
}
