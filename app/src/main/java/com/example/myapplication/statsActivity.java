package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class statsActivity extends AppCompatActivity {

    private TextView gpsData;
    private TextView hatchlingData;
    private StatsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        gpsData = findViewById(R.id.gpsData);
        hatchlingData = findViewById(R.id.hatchlingData);

        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        gpsData.setText("Location: loading data");
        hatchlingData.setText("Hatchling Status: loading data");
        // Observe LiveData for hatchling status updates
        viewModel.getHatchlingStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if (status != null) {
                    hatchlingData.setText(status);
                } else {
                    hatchlingData.setText("Hatchling Status: loading data");
                }
            }
        });

        // Observe LiveData for GPS location updates
        viewModel.getGpsLocation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String location) {
                if (location != null) {
                    gpsData.setText("Location: " + location);
                    Linkify.addLinks(gpsData, Linkify.WEB_URLS);
                } else {
                    gpsData.setText("Location: loading data");
                }
            }
        });

        // Start fetching data
        viewModel.fetchData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.cleanup();
    }
}