package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static String ServerUrl = "http://192.168.137.125";//token sending to where

    private static String deviceNameID;

    public static String getDeviceNameID() {
        return deviceNameID;
    }

    public void setDeviceNameID(String deviceNameID) {
        MainActivity.deviceNameID = deviceNameID;
    }

    public static String getServerUrl() {
        return ServerUrl;
    }

    public void setServerUrl(String serverUrl) {
        ServerUrl = serverUrl;
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout dynamicButtonContainer;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FCMTokenManager.registerToken(this);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        dynamicButtonContainer = findViewById(R.id.dynamicButtonContainer);
        errorTextView = findViewById(R.id.errorTextView);

        // Set up the swipe-to-refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDevicesFromFirebase();
            }
        });

        // Initial fetch of devices
        fetchDevicesFromFirebase();

        // Set margin top to 50% of screen height
        setErrorTextViewMargin();
    }

    private void setErrorTextViewMargin() {
        // Get the screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Calculate 50% of the screen height
        int marginTop = screenHeight / 3;

        // Set the margin top for the errorTextView
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) errorTextView.getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0); // left, top, right, bottom
        errorTextView.setLayoutParams(layoutParams);
    }

    private void fetchDevicesFromFirebase() {
        if (!isNetworkAvailable()) {
            showErrorTextView();
            swipeRefreshLayout.setRefreshing(false); // Stop refreshing animation if no network
            return;
        }

        // Reference to Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://esc491-eb2b5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("devices");

        // Read data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dynamicButtonContainer.removeAllViews(); // Clear existing views
                if (dataSnapshot.exists()) {
                    errorTextView.setVisibility(View.GONE);

                    for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                        if (deviceSnapshot.getKey().equals("tokens")) {
                            continue;
                        }
                        String deviceName = deviceSnapshot.child("name").getValue(String.class);
                        String deviceUrl = deviceSnapshot.child("url").getValue(String.class);

                        // Create a new button with the custom style
                        Button button = new Button(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme_DeviceButton), null, 0);
                        button.setText(deviceName);
                        button.setAllCaps(false);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setServerUrl(deviceUrl);
                                Intent intent = new Intent(MainActivity.this, DisplayMapActivity.class);
                                startActivity(intent);
                                setDeviceNameID(deviceName);
                            }
                        });

                        // Set layout parameters with margins
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(12, 60, 12, 0); // Set left, top, right, and bottom margins
                        button.setLayoutParams(layoutParams);

                        // Add the button to the dynamicButtonContainer
                        dynamicButtonContainer.addView(button);
                    }
                }
                else {
                    showErrorTextView();
                }

                // Stop the refreshing animation once data is loaded
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
                showErrorTextView();
                swipeRefreshLayout.setRefreshing(false); // Stop refreshing animation on error
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }
        return false;
    }

    private void showErrorTextView() {
        errorTextView.setVisibility(View.VISIBLE);
        dynamicButtonContainer.removeAllViews(); // Clear existing views
    }
}
