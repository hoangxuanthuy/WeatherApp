package com.example.weatherapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.data.api.ApiClient;
import com.example.weatherapp.data.api.WeatherApiService;
import com.example.weatherapp.data.model.GeocodingResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationPickerActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2001;

    private MapView mapView;
    private Marker selectedMarker;
    private TextView tvCityName;
    private final String OWM_API_KEY = "b7476a296b924c64969942901e41deb6";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_location_picker);

        mapView = findViewById(R.id.mapPicker);
        tvCityName = findViewById(R.id.tvCityName);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS); // ✅ hiển thị +/-
        mapView.getController().setZoom(6.5);
        mapView.getController().setCenter(new GeoPoint(16.0471, 108.2062)); // Mặc định Đà Nẵng

        // Cho phép click chuột để chọn vị trí
        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                addMarker(p);
                fetchCityName(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mapView.getOverlays().add(new MapEventsOverlay(mReceiver));

        // Cập nhật khi kéo bản đồ
        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                GeoPoint center = (GeoPoint) mapView.getMapCenter();
                addMarker(center);
                fetchCityName(center);
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        }));

        Button btnUseCurrent = findViewById(R.id.btnUseCurrentLocation);
        btnUseCurrent.setOnClickListener(v -> checkPermissionAndUseLocation());

        Button btnSelect = findViewById(R.id.btnSelectLocation);
        btnSelect.setOnClickListener(v -> {
            if (selectedMarker != null) {
                GeoPoint point = selectedMarker.getPosition();
                Intent result = new Intent();
                result.putExtra("lat", point.getLatitude());
                result.putExtra("lon", point.getLongitude());
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Vui lòng chọn vị trí trên bản đồ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarker(GeoPoint point) {
        if (selectedMarker != null) {
            mapView.getOverlays().remove(selectedMarker);
        }
        selectedMarker = new Marker(mapView);
        selectedMarker.setPosition(point);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        selectedMarker.setTitle("Đang xác định...");
        mapView.getOverlays().add(selectedMarker);
        mapView.invalidate();
    }

    private void fetchCityName(GeoPoint point) {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        service.getCityNameFromCoordinates(point.getLatitude(), point.getLongitude(), OWM_API_KEY, "vi")
                .enqueue(new Callback<List<GeocodingResponse>>() {
                    @Override
                    public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            String city = response.body().get(0).getName();
                            tvCityName.setText("Thành phố: " + city);
                            selectedMarker.setTitle(city);
                        } else {
                            tvCityName.setText("⚠️ Không xác định được thành phố tại vị trí này.");
                            selectedMarker.setTitle("Không rõ");
                            Toast.makeText(LocationPickerActivity.this,
                                    "Không thể lấy dữ liệu thời tiết tại vị trí này.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        mapView.invalidate();
                    }

                    @Override
                    public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                        tvCityName.setText("⚠️ Lỗi khi tải tên thành phố");
                        Toast.makeText(LocationPickerActivity.this,
                                "Kết nối thất bại. Thử lại sau.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkPermissionAndUseLocation() {
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
                        GeoPoint currentPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        mapView.getController().animateTo(currentPoint);
                        addMarker(currentPoint);
                        fetchCityName(currentPoint);
                    } else {
                        Toast.makeText(this, "Không thể lấy vị trí hiện tại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionAndUseLocation();
            } else {
                Toast.makeText(this, "Không có quyền truy cập vị trí.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
