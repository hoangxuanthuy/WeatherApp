package com.example.weatherapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.data.api.ApiClient;
import com.example.weatherapp.data.api.WeatherApiService;
import com.example.weatherapp.data.model.AirQualityResponse;
import com.example.weatherapp.data.model.CurrentWeather;
import com.example.weatherapp.data.model.ForecastResponse;
import com.example.weatherapp.data.model.GeocodingResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherHomeActivity extends BaseActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private AutoCompleteTextView searchView;
    private RecyclerView rvHourly;
    private HourlyAdapter hourlyAdapter;

    private FusedLocationProviderClient fusedLocationClient;
    private MapView osmMap;

    private ImageView ivWeatherIcon;
    private final String OWM_API_KEY = "b7476a296b924c64969942901e41deb6";

    private TextView tvCityName, tvTemperature, tvDescription;
    private TextView tvRainAmount, tvVisibilityDistance;
    private TextView tvHumidityValue, tvWindSpeedValue;
    private TextView tvSunriseTime, tvSunsetTime;
    private TextView tvFeelsLikeValue, tvAirQualityIndex, tvPressureValue;
    private TextView tvPm25, tvPm10, tvCo, tvNo2, tvO3, tvSo2;

    private final String[] CITY_SUGGESTIONS = new String[]{
            "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "Hải Phòng", "Huế", "Nha Trang", "Vũng Tàu", "Đà Lạt"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_home);

        initViews();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissionAndFetch();

        // Gợi ý và xử lý tìm kiếm
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CITY_SUGGESTIONS);
        searchView.setAdapter(adapter);
        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String city = adapter.getItem(position);
            getCurrentWeather(city);
            searchView.setText(""); // Reset sau khi chọn
        });

        searchView.setOnEditorActionListener((v, actionId, event) -> {
            String city = searchView.getText().toString().trim();
            if (!city.isEmpty()) {
                getCurrentWeather(city);
                searchView.setText("");
            }
            return true;
        });
    }

    private void initViews() {
        searchView = findViewById(R.id.searchView);
        rvHourly = findViewById(R.id.rvHourly);
        rvHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        tvRainAmount = findViewById(R.id.tvRain);
        tvVisibilityDistance = findViewById(R.id.tvVisibility);
        tvHumidityValue = findViewById(R.id.tvHumidity);
        tvWindSpeedValue = findViewById(R.id.tvWind);
        tvSunriseTime = findViewById(R.id.tvSunrise);
        tvSunsetTime = findViewById(R.id.tvSunset);
        tvFeelsLikeValue = findViewById(R.id.tvFeelsLike);
        tvAirQualityIndex = findViewById(R.id.tvAirQuality);
        tvPressureValue = findViewById(R.id.tvPressure);
        tvPm25 = findViewById(R.id.tvPm25);
        tvPm10 = findViewById(R.id.tvPm10);
        tvCo = findViewById(R.id.tvCo);
        tvNo2 = findViewById(R.id.tvNo2);
        tvO3 = findViewById(R.id.tvO3);
        tvSo2 = findViewById(R.id.tvSo2);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        osmMap = findViewById(R.id.osmMap);

        osmMap.setMultiTouchControls(true);
    }

    private void checkLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        getCityNameFromCoordinates(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Không thể lấy vị trí. Dùng mặc định.", Toast.LENGTH_SHORT).show();
                        getCurrentWeather("Hồ Chí Minh");
                    }
                });
    }

    private void getCityNameFromCoordinates(double lat, double lon) {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        service.getCityNameFromCoordinates(lat, lon, OWM_API_KEY, "vi").enqueue(new Callback<List<GeocodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    getCurrentWeather(response.body().get(0).getName());
                } else {
                    getCurrentWeather("Hồ Chí Minh");
                }
            }

            @Override
            public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                getCurrentWeather("Hồ Chí Minh");
            }
        });
    }

    private void getCurrentWeather(String city) {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        service.getCurrentWeatherByCity(city, OWM_API_KEY, "metric", "vi").enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrentWeather data = response.body();
                    updateWeatherUI(data);
                    getForecastData(data.getCoord().getLat(), data.getCoord().getLon());
                    getAirPollutionData(data.getCoord().getLat(), data.getCoord().getLon());
                    updateMap(data.getCoord().getLat(), data.getCoord().getLon(), city);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {}
        });
    }

    private void getForecastData(double lat, double lon) {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        service.getForecast(lat, lon, OWM_API_KEY, "metric", "vi").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hourlyAdapter = new HourlyAdapter(response.body().getList());
                    rvHourly.setAdapter(hourlyAdapter);
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {}
        });
    }

    private void getAirPollutionData(double lat, double lon) {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        service.getAirPollutionData(lat, lon, OWM_API_KEY).enqueue(new Callback<AirQualityResponse>() {
            @Override
            public void onResponse(Call<AirQualityResponse> call, Response<AirQualityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AirQualityResponse.Components c = response.body().getList().get(0).getComponents();
                    tvPm25.setText(String.format(Locale.getDefault(), "%.1f", c.getPm25()));
                    tvPm10.setText(String.format(Locale.getDefault(), "%.1f", c.getPm10()));
                    tvCo.setText(String.format(Locale.getDefault(), "%.1f", c.getCo()));
                    tvNo2.setText(String.format(Locale.getDefault(), "%.1f", c.getNo2()));
                    tvO3.setText(String.format(Locale.getDefault(), "%.1f", c.getO3()));
                    tvSo2.setText(String.format(Locale.getDefault(), "%.1f", c.getSo2()));
                }
            }

            @Override
            public void onFailure(Call<AirQualityResponse> call, Throwable t) {}
        });
    }

    private void updateWeatherUI(CurrentWeather data) {
        tvCityName.setText(data.getCityName());
        tvTemperature.setText(String.format(Locale.getDefault(), "%.2f°C", data.getMain().getTemp()));
        tvDescription.setText(data.getWeather().get(0).getDescription());
        tvFeelsLikeValue.setText(String.format("%.2f°C", data.getMain().getFeelsLike()));
        tvRainAmount.setText(data.getRain() != null ? data.getRain().getOneHour() + " mm" : "0 mm");
        tvVisibilityDistance.setText(String.format(Locale.getDefault(), "%.1f km", data.getVisibility() / 1000.0));

        long sunrise = data.getSys().getSunrise() * 1000L;
        long sunset = data.getSys().getSunset() * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvSunriseTime.setText(sdf.format(new Date(sunrise)));
        tvSunsetTime.setText(sdf.format(new Date(sunset)));

        Glide.with(this)
                .load("https://openweathermap.org/img/wn/" + data.getWeather().get(0).getIcon() + "@2x.png")
                .into(ivWeatherIcon);

        findViewById(R.id.rootLayout).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    private void updateMap(double lat, double lon, String city) {
        GeoPoint geoPoint = new GeoPoint(lat, lon);
        osmMap.getController().setZoom(10.0);
        osmMap.getController().setCenter(geoPoint);
        osmMap.getOverlays().clear();

        Marker marker = new Marker(osmMap);
        marker.setPosition(geoPoint);
        marker.setTitle(city);
        osmMap.getOverlays().add(marker);
        osmMap.invalidate();
    }
}
