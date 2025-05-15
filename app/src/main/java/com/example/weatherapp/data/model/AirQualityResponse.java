package com.example.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AirQualityResponse {

    @SerializedName("list")
    private List<AirData> list;

    public List<AirData> getList() { return list; }

    public static class AirData {
        @SerializedName("main")
        private Main main;

        @SerializedName("components")
        private Components components;

        public Main getMain() { return main; }

        public Components getComponents() { return components; }
    }

    public static class Main {
        @SerializedName("aqi")
        private int aqi;
        public int getAqi() { return aqi; }
    }

    public static class Components {
        @SerializedName("pm2_5")
        private float pm2_5;

        @SerializedName("pm10")
        private float pm10;

        @SerializedName("co")
        private float co;

        @SerializedName("no2")
        private float no2;

        @SerializedName("o3")
        private float o3;

        @SerializedName("so2")
        private float so2;

        public float getPm25() { return pm2_5; }
        public float getPm10() { return pm10; }
        public float getCo() { return co; }
        public float getNo2() { return no2; }
        public float getO3() { return o3; }
        public float getSo2() { return so2; }
    }
}
