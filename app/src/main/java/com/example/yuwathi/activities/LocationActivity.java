package com.example.yuwathi.activities;

// Import required Android classes
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

import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.example.yuwathi.services.FirebaseRealtimeDatabaseService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQ_LOCATION = 101;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuthService authService;
    private FirebaseRealtimeDatabaseService realtimeDatabaseService;
    private FirebaseFirestoreService firestoreService;
    private GoogleMap googleMap;
    private TextView tvAddress;

    private String currentUserId;
    private String currentUserName;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the location screen layout
        setContentView(R.layout.activity_location);

        authService = new FirebaseAuthService();
        realtimeDatabaseService = FirebaseRealtimeDatabaseService.getInstance();
        firestoreService = FirebaseFirestoreService.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseUser user = authService.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid();
        currentUserName = user.getEmail() != null ? user.getEmail() : "User";

        MaterialButton btnShare = findViewById(R.id.btn_share_location_action); // Share Location button
        MaterialButton btnStop = findViewById(R.id.btn_stop_sharing);           // Stop Sharing button
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);         // Bottom navigation bar
        ImageView btnBack = findViewById(R.id.btn_back);
        tvAddress = findViewById(R.id.tv_address);

        btnBack.setOnClickListener(v -> onBackPressed());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle Share Location button click - start sharing live location
        btnShare.setOnClickListener(v -> shareLocation());

        // Handle Stop Sharing button click - stop sharing location
        btnStop.setOnClickListener(v -> stopSharing());

        // Handle bottom navigation bar item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(LocationActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(LocationActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(LocationActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(LocationActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(LocationActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        ensureLocationAndRender();
    }

    private void ensureLocationAndRender() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                updateMapAndAddress(location);
            } else {
                tvAddress.setText("Unable to fetch location, please try again.");
            }
        });
    }

    private void updateMapAndAddress(Location location) {
        lastKnownLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Your current location"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        tvAddress.setText("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
    }

    private void shareLocation() {
        if (lastKnownLocation == null) {
            ensureLocationAndRender();
            Toast.makeText(this, "Getting current location...", Toast.LENGTH_SHORT).show();
            return;
        }

        double lat = lastKnownLocation.getLatitude();
        double lng = lastKnownLocation.getLongitude();

        realtimeDatabaseService.shareLocation(currentUserId, currentUserName, lat, lng,
                new FirebaseRealtimeDatabaseService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        firestoreService.saveLocation(currentUserId, lat, lng, new FirebaseFirestoreService.OnOperationCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(LocationActivity.this, "Live location sharing started", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(LocationActivity.this, "Shared live, history save failed: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LocationActivity.this, "Share failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void stopSharing() {
        realtimeDatabaseService.stopLocationSharing(currentUserId, new FirebaseRealtimeDatabaseService.OnOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(LocationActivity.this, "Location sharing stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LocationActivity.this, "Stop failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ensureLocationAndRender();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
