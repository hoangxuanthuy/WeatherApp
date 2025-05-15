package com.example.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;

public class IQAirResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }

    public Data getData() { return data; }

    public static class Data {

        @SerializedName("city")
        private String city;

        @SerializedName("state")
        private String state;

        @SerializedName("country")
        private String country;

        @SerializedName("location")
        private Location location;

        @SerializedName("current")
        private Current current;

        public String getCity() { return city; }

        public String getState() { return state; }

        public String getCountry() { return country; }

        public Location getLocation() { return location; }

        public Current getCurrent() { return current; }

        public static class Location {
            @SerializedName("type")
            private String type;

            @SerializedName("coordinates")
            private double[] coordinates;

            public String getType() { return type; }

            public double[] getCoordinates() { return coordinates; }
        }

        public static class Current {

            @SerializedName("pollution")
            private Pollution pollution;

            @SerializedName("weather")
            private Weather weather;

            public Pollution getPollution() { return pollution; }

            public Weather getWeather() { return weather; }

            public static class Pollution {

                @SerializedName("ts")
                private String ts;

                @SerializedName("aqius")
                private int aqius;

                @SerializedName("mainus")
                private String mainus;

                @SerializedName("aqicn")
                private int aqicn;

                @SerializedName("maincn")
                private String maincn;

                public String getTs() { return ts; }

                public int getAqiUS() { return aqius; }

                public String getMainUS() { return mainus; }

                public int getAqiCN() { return aqicn; }

                public String getMainCN() { return maincn; }


            }


            public static class Weather {

                @SerializedName("ts")
                private String ts;

                @SerializedName("ic")
                private String icon;

                @SerializedName("hu")
                private int humidity;

                @SerializedName("pr")
                private int pressure;

                @SerializedName("tp")
                private int temperature;

                @SerializedName("wd")
                private int windDirection;

                @SerializedName("ws")
                private double windSpeed;

                public String getTs() { return ts; }

                public String getIcon() { return icon; }

                public int getHumidity() { return humidity; }

                public int getPressure() { return pressure; }

                public int getTemperature() { return temperature; }

                public int getWindDirection() { return windDirection; }

                public double getWindSpeed() { return windSpeed; }
            }
        }
    }
}
