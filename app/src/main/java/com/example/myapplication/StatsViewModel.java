package com.example.myapplication;

import static com.example.myapplication.MainActivity.getDeviceNameID;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatsViewModel extends ViewModel {
    private FetchSensorDataTask fetchSensorDataTask;
    private FetchGpsDataTask fetchGpsDataTask;
    private MutableLiveData<String> hatchlingStatus = new MutableLiveData<>();
    private MutableLiveData<String> gpsLocation = new MutableLiveData<>();
    private MutableLiveData<LatLng> currentLocation = new MutableLiveData<>();

    public LiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng location) {
        currentLocation.setValue(location);
    }
    private int maxRefreshAttempts = 1;
    private int refreshAttempts = 0;

    private String serverUrl;


    public void setFetchSensorDataTask(FetchSensorDataTask fetchSensorDataTask) {
        this.fetchSensorDataTask = fetchSensorDataTask;
    }

    public void setFetchGpsDataTask(FetchGpsDataTask fetchGpsDataTask) {
        this.fetchGpsDataTask = fetchGpsDataTask;
    }

    public LiveData<String> getHatchlingStatus() {
        return hatchlingStatus;
    }

    public LiveData<String> getGpsLocation() {
        return gpsLocation;
    }

    public void setHatchlingStatus(MutableLiveData<String> hatchlingStatus) {
        this.hatchlingStatus = hatchlingStatus;
    }

    public void setGpsLocation(MutableLiveData<String> gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public void fetchData() {
        fetchSensorDataTask = new FetchSensorDataTask(MainActivity.getServerUrl());
        fetchGpsDataTask = new FetchGpsDataTask(MainActivity.getServerUrl());

        // Execute tasks asynchronously
        fetchSensorDataTask.execute();
        fetchGpsDataTask.execute();

        // Check data every second until both tasks return non-null values or reach max attempts
        checkData();
    }

    private void checkData() {
        if (refreshAttempts >= maxRefreshAttempts) {
//            hatchlingStatus.setValue("Device is ASLEEP");
            fetchAndSetHatchlingStatus();
            return; // Stop refreshing if max attempts reached
        }

        if (fetchSensorDataTask.getResult() != null && fetchGpsDataTask.getGoogleMapsUrl() != null) {
            hatchlingStatus.setValue(fetchSensorDataTask.getResult().equals("1") ?
                    "Hatchling Status: Hatched" : "Hatchling Status: Not hatched yet");
            gpsLocation.setValue(fetchGpsDataTask.getGoogleMapsUrl());
        } else {
            refreshAttempts++;
            // Retry after 1 second
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkData();
                }
            }, 1000);
        }
    }

    private void fetchAndSetHatchlingStatus() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://esc491-eb2b5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("devices");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                    if (getDeviceNameID().equals(deviceSnapshot.child("name").getValue(String.class))) {
                        String sensor = deviceSnapshot.child("sensor").getValue(String.class);
                        Double lat = deviceSnapshot.child("Latitude").getValue(Double.class);
                        Double lon = deviceSnapshot.child("Longitude").getValue(Double.class);
                        String locationUrl = "https://www.google.com/maps?q=" + lat + "," + lon;

                        String statusMessage = "Hacthling Status: " +
                                (sensor.equals("1") ? "Hatched" : "Not hatched yet");
                        gpsLocation.setValue(locationUrl);

                        hatchlingStatus.setValue(statusMessage);

                        return; // Data found, stop the loop
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hatchlingStatus.setValue("Error fetching Cached Data from database");
            }
        });
    }

    public void cleanup() {
        if (fetchSensorDataTask != null) {
            fetchSensorDataTask.cancel(true);
        }
        if (fetchGpsDataTask != null) {
            fetchGpsDataTask.cancel(true);
        }
    }

}