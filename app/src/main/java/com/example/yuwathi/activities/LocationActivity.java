package com.example.yuwathi.activities;

// Android core imports
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// App resources and services
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.example.yuwathi.services.FirebaseRealtimeDatabaseService;

// Google Maps & Location services
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// UI components
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

/**
 * LocationActivity
 * -----------------
 * This screen allows the user to:
 * - View their current location on Google Maps
 * - Share live location to Firebase Realtime Database
 * - Save location history to Firestore
 * - Stop live location sharing
 */
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Permission request code for location access
    private static final int REQ_LOCATION = 101;

    // Location & Firebase services
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuthService authService;
    private FirebaseRealtimeDatabaseService realtimeDatabaseService;
    private FirebaseFirestoreService firestoreService;

    // Google Map instance
    private GoogleMap googleMap;

    // UI elements
    private TextView tvAddress;

    // Current user details
    private String currentUserId;
    private String currentUserName;

    // Cached last known location (used for sharing)
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load UI layout
        setContentView(R.layout.activity_location);

        // Initialize Firebase services
        authService = new FirebaseAuthService();
        realtimeDatabaseService = FirebaseRealtimeDatabaseService.getInstance();
        firestoreService = FirebaseFirestoreService.getInstance();

        // Enable device location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get currently logged-in user
        FirebaseUser user = authService.getCurrentUser();

        // If user is not logged in, redirect to Login screen
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Store user details
        currentUserId = user.getUid();
        currentUserName = user.getEmail() != null ? user.getEmail() : "User";

        // UI components
        MaterialButton btnShare = findViewById(R.id.btn_share_location_action);
        MaterialButton btnStop = findViewById(R.id.btn_stop_sharing);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        ImageView btnBack = findViewById(R.id.btn_back);
        tvAddress = findViewById(R.id.tv_address);

        // Back button action
        btnBack.setOnClickListener(v -> onBackPressed());

        // Initialize Google Map fragment
        //Load Google Map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Share current location to Firebase
        btnShare.setOnClickListener(v -> shareLocation());

        // Stop live location sharing
        btnStop.setOnClickListener(v -> stopSharing());

        // Bottom navigation handling
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            }
            else if (id == R.id.nav_contacts) {
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            }
            else if (id == R.id.nav_sos) {
                startActivity(new Intent(this, SosActivity.class));
                return true;
            }
            else if (id == R.id.nav_tips) {
                startActivity(new Intent(this, SafetyTipsActivity.class));
                return true;
            }
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    /**
     * Called when Google Map is ready to use
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) { //Called when Google Map is ready
        googleMap = map;
        ensureLocationAndRender();
        //Save map object
         //Start location process
    }

    /**
     * Checks location permission and loads last known location
     */
    private void ensureLocationAndRender() {

        // Check runtime location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            // Request permission if not granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQ_LOCATION
            );
            return;
        }

        // Enable blue dot on map (user location)
        googleMap.setMyLocationEnabled(true);

        // Get last known location from device
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        updateMapAndAddress(location);
                    } else {
                        tvAddress.setText("Unable to fetch location, please try again.");
                    }
                });
    }

    /**
     * Updates map marker and UI with current location
     */
    private void updateMapAndAddress(Location location) {

        // Store location for reuse (sharing without refetching)
        lastKnownLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Clear old marker and add new one
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your current location"));

        // Move camera to current location
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        // Show coordinates on UI
        tvAddress.setText("Lat: " + location.getLatitude()
                + ", Lng: " + location.getLongitude());
    }

    /**
     * Shares live location to Firebase (Realtime + Firestore)
     */
    private void shareLocation() {

        // If location not yet loaded
        if (lastKnownLocation == null) {
            ensureLocationAndRender();
            Toast.makeText(this, "Getting current location...", Toast.LENGTH_SHORT).show();
            return;
        }

        double lat = lastKnownLocation.getLatitude();
        double lng = lastKnownLocation.getLongitude();

        // Save live location in Realtime Database
        realtimeDatabaseService.shareLocation(
                currentUserId,
                currentUserName,
                lat,
                lng,
                new FirebaseRealtimeDatabaseService.OnOperationCallback() {

                    @Override
                    public void onSuccess() {

                        // Save history in Firestore after realtime update
                        firestoreService.saveLocation(
                                currentUserId,
                                lat,
                                lng,
                                new FirebaseFirestoreService.OnOperationCallback() {

                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(
                                                LocationActivity.this,
                                                "Live location sharing started",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(
                                                LocationActivity.this,
                                                "Location shared, but history save failed.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                LocationActivity.this,
                                "Could not share location. Check connection/Firebase rules.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Stops live location sharing
     */
    private void stopSharing() {
        realtimeDatabaseService.stopLocationSharing(
                currentUserId,
                new FirebaseRealtimeDatabaseService.OnOperationCallback() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                LocationActivity.this,
                                "Location sharing stopped",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                LocationActivity.this,
                                "Could not stop sharing right now.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Handles runtime permission result
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Retry loading location after permission granted
                ensureLocationAndRender();

            } else {
                Toast.makeText(
                        this,
                        "Location permission is required",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}