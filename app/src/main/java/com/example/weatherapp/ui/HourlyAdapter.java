package com.example.weatherapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.data.model.ForecastResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder> {

    private final List<ForecastResponse.ForecastItem> hourlyList;

    public HourlyAdapter(List<ForecastResponse.ForecastItem> hourlyList) {
        this.hourlyList = hourlyList;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = hourlyList.get(position);

        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdfInput.parse(item.getDtTxt());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            holder.tvHour.setText(sdfOutput.format(date));
        } catch (Exception e) {
            holder.tvHour.setText("N/A");
        }

        holder.tvTemp.setText(Math.round(item.getMain().getTemp()) + "°C");

        String icon = item.getWeather().get(0).getIcon();
        Glide.with(holder.itemView.getContext())
                .load("https://openweathermap.org/img/wn/" + icon + "@2x.png")
                .into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }

    public static class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour, tvTemp;
        ImageView ivIcon;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tvHour);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
