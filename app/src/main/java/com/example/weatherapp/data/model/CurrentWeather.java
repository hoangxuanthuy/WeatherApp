package com.example.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrentWeather {

    @SerializedName("coord")
    private Coord coord;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("base")
    private String base;

    @SerializedName("main")
    private Main main;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("dt")
    private long dt;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("timezone")
    private int timezone;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String cityName;

    @SerializedName("cod")
    private int cod;

    @SerializedName("rain")
    private Rain rain;

    public Coord getCoord() { return coord; }
    public List<Weather> getWeather() { return weather; }
    public Main getMain() { return main; }
    public int getVisibility() { return visibility; }
    public Wind getWind() { return wind; }
    public Clouds getClouds() { return clouds; }
    public Sys getSys() { return sys; }
    public String getCityName() { return cityName; }
    public Rain getRain() { return rain; }

    public static class Coord {
        @SerializedName("lon")
        private double lon;

        @SerializedName("lat")
        private double lat;

        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    public static class Weather {
        @SerializedName("id")
        private int id;

        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class Main {
        @SerializedName("temp")
        private float temp;

        @SerializedName("feels_like")
        private float feelsLike;

        @SerializedName("temp_min")
        private float tempMin;

        @SerializedName("temp_max")
        private float tempMax;

        @SerializedName("pressure")
        private int pressure;

        @SerializedName("humidity")
        private int humidity;

        public float getTemp() { return temp; }
        public float getFeelsLike() { return feelsLike; }
        public float getTempMin() { return tempMin; }
        public float getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        @SerializedName("deg")
        private float deg;

        public float getSpeed() { return speed; }
        public float getDeg() { return deg; }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() { return all; }
    }

    public static class Sys {
        @SerializedName("country")
        private String country;

        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }

    public static class Rain {
        @SerializedName("1h")
        private Float oneHour;

        public Float getOneHour() {
            return oneHour != null ? oneHour : 0f;
        }
    }
}
