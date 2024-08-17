package com.example.myapplication;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityDisplayMapBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class DisplayMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private TextView gpsData;
    private TextView hatchlingData;
    private StatsViewModel viewModel;
    private GoogleMap mMap;
    private ActivityDisplayMapBinding binding;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private Button directionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hatchlingData = findViewById(R.id.hatchlingData);

        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        hatchlingData.setText("Hatchling Status: Loading data...");

        // Observe LiveData for hatchling status updates
        viewModel.getHatchlingStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if (status != null) {
                    hatchlingData.setText(status);
                } else {
                    hatchlingData.setText("Hatchling Status: Loading data...");
                }
            }
        });

        // Initialize BottomSheetBehavior
        LinearLayout bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Set initial state to expanded
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Calculate the desired height (40% of the screen height)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int desiredHeight = (int) (screenHeight * 0.4);

        // Set the desired height as the peek height
        bottomSheetBehavior.setPeekHeight(desiredHeight);

        TextView view1 = findViewById(R.id.devInfo);
        view1.setText(MainActivity.getDeviceNameID() + " Status");

        directionButton = findViewById(R.id.directionsButton);
        directionButton.setEnabled(false);
        directionButton.setTextColor(getResources().getColor(R.color.faded_color));
        Drawable icon = getResources().getDrawable(R.drawable.direction_external_link);
        DrawableCompat.setTint(icon, getResources().getColor(R.color.faded_color));
        directionButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        // Start fetching data
        viewModel.fetchData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.cleanup();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Observe LiveData for GPS location updates
        viewModel.getGpsLocation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String location) {
                if (location != null) {
                    String[] parts = location.split(",");
                    String latitudeStr = parts[0].substring(parts[0].indexOf('=') + 1);
                    String longitudeStr = parts[1];

                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);

                    LatLng newPosition = new LatLng(latitude, longitude);

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(newPosition).title("Nest Location").icon(getBitmapDescriptorFromVector(R.drawable.turtle_marker)));

                    // Zoom in to the marker initially
                    float zoomLevel = 16.0f; // You can adjust the zoom level as needed
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, zoomLevel));

                    // Set the current location in ViewModel
                    viewModel.setCurrentLocation(newPosition);

                    directionButton.setTextColor(getResources().getColor(R.color.not_faded_color));
                    Drawable icon = getResources().getDrawable(R.drawable.direction_external_link);
                    DrawableCompat.setTint(icon, getResources().getColor(R.color.not_faded_color));
                    directionButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                    directionButton.setEnabled(true);
                }
            }
        });
    }

    public void getDirections(View view) {
        // Check if the map is ready and the marker position is available
        if (mMap != null && viewModel.getCurrentLocation() != null) {
            LatLng currentLocation = viewModel.getCurrentLocation().getValue();

            // Construct the intent to launch Google Maps with directions
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentLocation.latitude + "," + currentLocation.longitude + "&mode=d");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Check if there is an app that can handle this intent
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                // Launch the Google Maps intent
                startActivity(mapIntent);
            }
        }
    }

    private BitmapDescriptor getBitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
