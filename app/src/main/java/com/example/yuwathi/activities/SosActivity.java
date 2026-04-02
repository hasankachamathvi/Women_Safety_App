package com.example.yuwathi.activities;

// Import required Android classes
import android.Manifest;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.example.yuwathi.services.FirebaseRealtimeDatabaseService;
import com.example.yuwathi.utils.SOSHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SosActivity extends AppCompatActivity {

    private static final int REQ_LOCATION = 102; // Request code for location permission

    private CountDownTimer countDownTimer;  // Timer for the 3-second countdown
    private boolean isActivated = false;    // Flag to track if SOS is already activated
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private FirebaseRealtimeDatabaseService realtimeDatabaseService;
    private FusedLocationProviderClient fusedLocationClient;
    private SOSHelper sosHelper;
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the SOS screen layout
        setContentView(R.layout.activity_sos);

        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();
        realtimeDatabaseService = FirebaseRealtimeDatabaseService.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sosHelper = new SOSHelper(this);

        FirebaseUser user = authService.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            currentUserName = user.getEmail() != null ? user.getEmail() : "User";
        } else {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find buttons and text views from the layout
        MaterialButton btnSos = findViewById(R.id.btn_sos_main);     // Main SOS button (hold to activate)
        MaterialButton btnCancel = findViewById(R.id.btn_cancel_sos); // Cancel button
        TextView tvStatus = findViewById(R.id.tv_sos_status);        // Status text (shows instructions)
        TextView tvCountdown = findViewById(R.id.tv_countdown);      // Countdown number display

        // Handle SOS button touch events (hold to activate)
        btnSos.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // User started pressing - begin countdown
                    if (!isActivated) startCountdown(tvCountdown, tvStatus);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // User released the button - cancel countdown
                    if (!isActivated) cancelCountdown(tvCountdown, tvStatus);
                    return true;
                default:
                    return false;
            }
        });

        // Handle Cancel button click - cancel and go back
        btnCancel.setOnClickListener(v -> {
            cancelCountdown(tvCountdown, tvStatus);
            finish(); // Close SOS page
        });
    }

    private void startCountdown(TextView tvCountdown, TextView tvStatus) {
        tvStatus.setText(getString(R.string.sos_hold_message)); // Show "Keep holding" message
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update countdown number each second
                tvCountdown.setText(String.valueOf((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                // Countdown finished - SOS is now activated!
                isActivated = true;
                tvCountdown.setText("");
                tvStatus.setText(getString(R.string.sos_activated));
                activateSOS(); // Send the SOS alert
            }
        }.start();
    }

    private void cancelCountdown(TextView tvCountdown, TextView tvStatus) {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the timer
        }
        tvCountdown.setText("");
        tvStatus.setText(getString(R.string.sos_hold_message));
    }

    private void activateSOS() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION);
            return;
        }

        // Get the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                // If location is not available, send SOS with default coordinates (0,0)
                sendSosWithCoordinates(0.0, 0.0);
            } else {
                // Send SOS with the actual latitude and longitude
                sendSosWithCoordinates(location.getLatitude(), location.getLongitude());
            }
        }).addOnFailureListener(e -> sendSosWithCoordinates(0.0, 0.0));
    }

    private void sendSosWithCoordinates(double latitude, double longitude) {
        // Build the SOS message with coordinates
        String message = sosHelper.buildSosMessage(latitude, longitude);
        realtimeDatabaseService.sendSOSAlert(currentUserId, currentUserName, latitude, longitude,
                message, new FirebaseRealtimeDatabaseService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(SosActivity.this, "SOS alert sent", Toast.LENGTH_LONG).show();
                        promptEmergencyContactAlert(message); // Prompt to notify emergency contacts
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(SosActivity.this, "Failed to send SOS: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void promptEmergencyContactAlert(String message) {
        // Retrieve emergency contacts and prompt the user to notify them
        firestoreService.getEmergencyContacts(currentUserId, new FirebaseFirestoreService.OnContactsListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> contacts) {
                List<String> numbers = new ArrayList<>();
                for (Map<String, Object> contact : contacts) {
                    Object phone = contact.get("phone");
                    if (phone != null) {
                        numbers.add(String.valueOf(phone));
                    }
                }

                // Show a dialog to notify emergency contacts via SMS or call
                new AlertDialog.Builder(SosActivity.this)
                        .setTitle("Notify Emergency Contacts")
                        .setMessage(numbers.isEmpty()
                                ? "No emergency contacts found. You can call emergency services now."
                                : "SOS is saved. Do you want to open your SMS app to notify saved emergency contacts?")
                        .setPositiveButton(numbers.isEmpty() ? "Call 119" : "Open SMS", (dialog, which) -> {
                            if (numbers.isEmpty()) {
                                sosHelper.openEmergencyDialer(); // Open dialer with emergency number
                            } else {
                                sosHelper.openSmsComposer(numbers, message); // Open SMS composer with contact numbers
                            }
                        })
                        .setNegativeButton("Close", null)
                        .show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SosActivity.this, "Contacts not loaded: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activateSOS(); // Retry SOS activation if permission is granted
            } else {
                sendSosWithCoordinates(0.0, 0.0); // Send SOS with default coordinates if permission is denied
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer when activity is destroyed
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
