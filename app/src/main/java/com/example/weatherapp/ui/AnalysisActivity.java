package com.example.weatherapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.weatherapp.R;
import com.example.weatherapp.data.api.ApiClient;
import com.example.weatherapp.data.api.WeatherApiService;
import com.example.weatherapp.data.model.ForecastResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisActivity extends BaseActivity {

    private Spinner spinnerComponent;
    private Button btnPickTime, btnViewChart;
    private LineChart chart;
    private BarChart barChart;
    private TextView txtEmpty;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        spinnerComponent = findViewById(R.id.spinnerComponent);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnViewChart = findViewById(R.id.btnViewChart);
        chart = findViewById(R.id.chart);
        barChart = findViewById(R.id.barChart);
        txtEmpty = findViewById(R.id.txtEmpty);

        setupComponentSpinner();
        setupBottomNav();

        btnPickTime.setOnClickListener(v -> showDateTimePickers());
        btnViewChart.setOnClickListener(v -> fetchForecastData());
    }

    private void setupComponentSpinner() {
        List<String> components = Arrays.asList(
                getString(R.string.component_temperature),
                getString(R.string.component_rain),
                getString(R.string.component_humidity)
        );


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                components
        );
        spinnerComponent.setAdapter(adapter);
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_analysis);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, WeatherHomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_analysis) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void showDateTimePickers() {
        DatePickerDialog startPicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    startDate.set(year, month, dayOfMonth);
                    DatePickerDialog endPicker = new DatePickerDialog(this,
                            (v, y, m, d) -> {
                                endDate.set(y, m, d);
                                Toast.makeText(this, "Khoảng thời gian: " +
                                        dateFormat.format(startDate.getTime()) + " → " +
                                        dateFormat.format(endDate.getTime()), Toast.LENGTH_SHORT).show();
                            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                    endPicker.show();
                }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        startPicker.show();
    }

    private void fetchForecastData() {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        String apiKey = "b7476a296b924c64969942901e41deb6";

        Call<ForecastResponse> call = service.getForecast("Bien Hoa", apiKey, "metric", "vi");
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForecastResponse> call, @NonNull Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    drawChart(response.body());
                } else {
                    txtEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastResponse> call, @NonNull Throwable t) {
                txtEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void drawChart(ForecastResponse forecast) {
        List<Entry> entries = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();

        int index = 0;
        String selectedComponent = spinnerComponent.getSelectedItem().toString();

        for (ForecastResponse.ForecastItem item : forecast.getList()) {
            Date dt = new Date(item.getDt() * 1000L);
            if (dt.after(startDate.getTime()) && dt.before(endDate.getTime())) {
                float yValue;
                switch (selectedComponent) {
                    case "Lượng mưa (mm)":
                        yValue = item.getRain() != null && item.getRain().getThreeHour() != null ?
                                item.getRain().getThreeHour().floatValue() : 0f;
                        break;
                    case "Độ ẩm (%)":
                        yValue = (float) item.getMain().getHumidity();
                        break;
                    default:
                        yValue = (float) item.getMain().getTemp();
                        break;
                }
                entries.add(new Entry(index, yValue));
                barEntries.add(new BarEntry(index, yValue));
                xLabels.add(new SimpleDateFormat("dd-MM HH:mm", Locale.getDefault()).format(dt));
                index++;
            }
        }

        if (entries.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            chart.clear();
            barChart.clear();
            return;
        } else {
            txtEmpty.setVisibility(View.GONE);
        }

        // Line chart setup
        LineDataSet lineDataSet = new LineDataSet(entries, selectedComponent);
        lineDataSet.setColor(Color.parseColor("#6200EE"));
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setCircleColor(Color.parseColor("#03DAC5"));
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextSize(10f);

        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        chart.getDescription().setText("Biểu đồ từ " + dateFormat.format(startDate.getTime()) + " đến " + dateFormat.format(endDate.getTime()));
        chart.setExtraOffsets(10f, 10f, 10f, 36f);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                return (i >= 0 && i < xLabels.size()) ? xLabels.get(i) : "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setTextSize(10f);

        chart.getAxisLeft().setTextColor(Color.DKGRAY);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.DKGRAY);
        chart.animateX(800);
        chart.invalidate();

        // Bar chart setup
        BarDataSet barDataSet = new BarDataSet(barEntries, selectedComponent);
        barDataSet.setColor(Color.parseColor("#FF6200EE"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        barDataSet.setDrawValues(true);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);
        barChart.getDescription().setText("Biểu đồ từ " + dateFormat.format(startDate.getTime()) + " đến " + dateFormat.format(endDate.getTime()));

        XAxis barXAxis = barChart.getXAxis();
        barXAxis.setValueFormatter(xAxis.getValueFormatter());
        barXAxis.setGranularity(1f);
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barXAxis.setLabelRotationAngle(45f);
        barXAxis.setTextSize(10f);
        barXAxis.setDrawGridLines(false);

        YAxis barYAxisRight = barChart.getAxisRight();
        barYAxisRight.setEnabled(false);
        barChart.getAxisLeft().setTextColor(Color.DKGRAY);
        barChart.getLegend().setTextColor(Color.DKGRAY);

        barChart.setExtraOffsets(10f, 10f, 10f, 36f);
        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);

        barChart.animateY(800);
        barChart.invalidate();
    }
}