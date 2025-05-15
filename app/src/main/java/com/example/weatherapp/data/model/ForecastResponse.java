package com.example.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    private List<ForecastItem> list;

    public List<ForecastItem> getList() {
        return list;
    }

    public static class ForecastItem {

        @SerializedName("dt")
        private long dt;

        @SerializedName("dt_txt")
        private String dtTxt;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        @SerializedName("wind")
        private Wind wind;

        @SerializedName("clouds")
        private Clouds clouds;

        @SerializedName("pop")
        private float pop;

        @SerializedName("rain")
        private Rain rain;

        public long getDt() { return dt; }
        public String getDtTxt() { return dtTxt; }
        public Main getMain() { return main; }
        public List<Weather> getWeather() { return weather; }
        public Wind getWind() { return wind; }
        public Clouds getClouds() { return clouds; }
        public float getPop() { return pop; }
        public Rain getRain() { return rain; }
    }

    public static class Main {
        @SerializedName("temp")
        private float temp;

        @SerializedName("feels_like")
        private float feelsLike;

        @SerializedName("pressure")
        private int pressure;

        @SerializedName("humidity")
        private int humidity;

        public float getTemp() { return temp; }
        public float getFeelsLike() { return feelsLike; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class Weather {
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

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        @SerializedName("deg")
        private int deg;

        public float getSpeed() { return speed; }
        public int getDeg() { return deg; }
    }

    public static class Clouds {
        @SerializedName("all")
        private int all;

        public int getAll() { return all; }
    }

    public static class Rain {
        @SerializedName("3h")
        private Float threeHour;

        public Float getThreeHour() {
            return threeHour != null ? threeHour : 0f;
        }
    }
}
