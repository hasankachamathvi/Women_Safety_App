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
import java.util.Locale;
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

        // Step 1: Send SOS alert to database
        realtimeDatabaseService.sendSOSAlert(currentUserId, currentUserName, latitude, longitude,
                message, new FirebaseRealtimeDatabaseService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(SosActivity.this, "SOS alert sent!", Toast.LENGTH_LONG).show();

                        // Step 2: Share location with emergency contacts
                        realtimeDatabaseService.shareLocation(currentUserId, currentUserName, latitude, longitude,
                                new FirebaseRealtimeDatabaseService.OnOperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        // Step 3: Send SMS to emergency contacts
                                        sendAlertsToEmergencyContacts(message, latitude, longitude);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(SosActivity.this, "Location sharing failed: " + error, Toast.LENGTH_SHORT).show();
                                        sendAlertsToEmergencyContacts(message, latitude, longitude);
                                    }
                                });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(SosActivity.this, "Failed to send SOS: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendAlertsToEmergencyContacts(String message, double latitude, double longitude) {
        // Retrieve emergency contacts and automatically send SMS
        firestoreService.getEmergencyContacts(currentUserId, new FirebaseFirestoreService.OnContactsListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> contacts) {
                if (contacts == null || contacts.isEmpty()) {
                    showEmergencyOptionsDialog(message, latitude, longitude, new ArrayList<>());
                    return;
                }

                // Extract phone numbers and contact names
                List<String> phoneNumbers = new ArrayList<>();
                List<String> contactNames = new ArrayList<>();

                for (Map<String, Object> contact : contacts) {
                    Object phone = contact.get("phone");
                    Object name = contact.get("name");

                    if (phone != null) {
                        phoneNumbers.add(String.valueOf(phone));
                        if (name != null) {
                            contactNames.add(String.valueOf(name));
                        }
                    }
                }

                if (!phoneNumbers.isEmpty()) {
                    // Automatically send SMS to all emergency contacts
                    String locationUrl = String.format(Locale.getDefault(),
                            "https://maps.google.com/?q=%f,%f", latitude, longitude);
                    String smsMessage = String.format(Locale.getDefault(),
                            "🚨 EMERGENCY SOS ALERT 🚨\n\n" +
                            "User: %s\n" +
                            "Location: %s\n\n" +
                            "They need help! Please check on them immediately.",
                            currentUserName, locationUrl);

                    sosHelper.openSmsComposer(phoneNumbers, smsMessage);
                    showEmergencyAlertSummary(contactNames, phoneNumbers.size());
                }

                showEmergencyOptionsDialog(message, latitude, longitude, phoneNumbers);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SosActivity.this, "Could not load emergency contacts: " + error, Toast.LENGTH_SHORT).show();
                showEmergencyOptionsDialog(message, latitude, longitude, new ArrayList<>());
            }
        });
    }

    private void showEmergencyAlertSummary(List<String> contactNames, int contactCount) {
        if (contactNames.isEmpty()) {
            return;
        }

        String contacts = android.text.TextUtils.join(", ", contactNames);
        String message = String.format(Locale.getDefault(),
                "SOS alert activated!\n\nAlerts sent to %d contact(s):\n%s\n\nLocation has been shared.",
                contactCount, contacts);

        new AlertDialog.Builder(this)
                .setTitle("🚨 SOS Activated")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showEmergencyOptionsDialog(String message, double latitude, double longitude, List<String> phoneNumbers) {
        new AlertDialog.Builder(this)
                .setTitle("Emergency SOS Activated")
                .setMessage(phoneNumbers.isEmpty()
                        ? "No emergency contacts found.\n\nYour location has been shared.\n\nWould you like to call emergency services?"
                        : "SOS alerts sent to emergency contacts!\n\nYour location has been shared.\n\nWould you like to call emergency services?")
                .setPositiveButton(phoneNumbers.isEmpty() ? "Call 119" : "Call Emergency", (dialog, which) -> {
                    sosHelper.openEmergencyDialer();
                })
                .setNegativeButton("Close", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Deprecated
    // This method is replaced by sendAlertsToEmergencyContacts
    private void promptEmergencyContactAlert(String message) {
        // Old implementation - use sendAlertsToEmergencyContacts instead
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
