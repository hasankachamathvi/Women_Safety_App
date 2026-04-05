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

/**
 * SOSActivity
 * -----------
 * Handles emergency SOS activation, location capture, and alert dispatch.
 * Flow:
 * Hold SOS button → countdown → activate SOS → get location → send alerts
 */
public class SosActivity extends AppCompatActivity {

    private static final int REQ_LOCATION = 102; // Permission request code

    // Countdown timer for SOS hold action
    private CountDownTimer countDownTimer;

    // Prevent multiple SOS triggers
    private boolean isActivated = false;

    // Firebase services
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private FirebaseRealtimeDatabaseService realtimeDatabaseService;

    // Location provider
    private FusedLocationProviderClient fusedLocationClient;

    // Helper for SMS and emergency actions
    private SOSHelper sosHelper;

    // Current user data
    private String currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load SOS UI layout
        setContentView(R.layout.activity_sos);

        // Initialize Firebase services
        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();
        realtimeDatabaseService = FirebaseRealtimeDatabaseService.getInstance();

        // Initialize location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Helper class for SMS, dialer, message building
        sosHelper = new SOSHelper(this);

        // Get current logged-in user
        FirebaseUser user = authService.getCurrentUser();

        if (user != null) {
            currentUserId = user.getUid();
            currentUserName = user.getEmail() != null ? user.getEmail() : "User";
        } else {
            // If no user logged in, stop activity
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI elements
        MaterialButton btnSos = findViewById(R.id.btn_sos_main);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel_sos);
        TextView tvStatus = findViewById(R.id.tv_sos_status);
        TextView tvCountdown = findViewById(R.id.tv_countdown);

        // SOS button touch listener (hold to activate)
        btnSos.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                // User starts pressing button → start countdown
                case MotionEvent.ACTION_DOWN:
                    if (!isActivated) startCountdown(tvCountdown, tvStatus);
                    return true;

                // User releases or cancels → stop countdown
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!isActivated) cancelCountdown(tvCountdown, tvStatus);
                    return true;

                default:
                    return false;
            }
        });

        // Cancel button → stop SOS process and close screen
        btnCancel.setOnClickListener(v -> {
            cancelCountdown(tvCountdown, tvStatus);
            finish();
        });
    }

    /**
     * Starts 3-second countdown before activating SOS
     */
    private void startCountdown(TextView tvCountdown, TextView tvStatus) {

        // Show instruction message
        tvStatus.setText(getString(R.string.sos_hold_message));

        // 3 second countdown timer
        countDownTimer = new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Update countdown every second
                tvCountdown.setText(String.valueOf((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                // Mark SOS as activated
                isActivated = true;

                // Clear UI
                tvCountdown.setText("");

                // Update status
                tvStatus.setText(getString(R.string.sos_activated));

                // Trigger SOS process
                activateSOS();
            }
        }.start();
    }

    /**
     * Cancels SOS countdown
     */
    private void cancelCountdown(TextView tvCountdown, TextView tvStatus) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        tvCountdown.setText("");
        tvStatus.setText(getString(R.string.sos_hold_message));
    }

    /**
     * Main SOS activation flow
     */
    private void activateSOS() {

        // Check location permission
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

        // Get last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            if (location == null) {
                // If location not available → send default coordinates
                sendSosWithCoordinates(0.0, 0.0);
            } else {
                sendSosWithCoordinates(
                        location.getLatitude(),
                        location.getLongitude()
                );
            }

        }).addOnFailureListener(e ->
                sendSosWithCoordinates(0.0, 0.0)
        );
    }

    /**
     * Sends SOS alert with coordinates
     */
    private void sendSosWithCoordinates(double latitude, double longitude) {

        // Build SOS message
        String message = sosHelper.buildSosMessage(latitude, longitude);

        // Step 1: Send SOS to Realtime Database
        realtimeDatabaseService.sendSOSAlert(
                currentUserId,
                currentUserName,
                latitude,
                longitude,
                message,
                new FirebaseRealtimeDatabaseService.OnOperationCallback() {

                    @Override
                    public void onSuccess() {

                        Toast.makeText(SosActivity.this,
                                "SOS alert sent!",
                                Toast.LENGTH_LONG).show();

                        // Step 2: Share live location
                        realtimeDatabaseService.shareLocation(
                                currentUserId,
                                currentUserName,
                                latitude,
                                longitude,
                                new FirebaseRealtimeDatabaseService.OnOperationCallback() {

                                    @Override
                                    public void onSuccess() {
                                        // Step 3: Send alerts to contacts
                                        sendAlertsToEmergencyContacts(
                                                message,
                                                latitude,
                                                longitude
                                        );
                                    }

                                    @Override
                                    public void onError(String error) {
                                        // Continue even if location sharing fails
                                        Toast.makeText(
                                                SosActivity.this,
                                                "Location sharing failed: " + error,
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        sendAlertsToEmergencyContacts(
                                                message,
                                                latitude,
                                                longitude
                                        );
                                    }
                                });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                SosActivity.this,
                                "Failed to send SOS: " + error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Sends alerts to emergency contacts from Firestore
     */
    private void sendAlertsToEmergencyContacts(
            String message,
            double latitude,
            double longitude
    ) {

        firestoreService.getEmergencyContacts(
                currentUserId,
                new FirebaseFirestoreService.OnContactsListCallback() {

                    @Override
                    public void onSuccess(List<Map<String, Object>> contacts) {

                        if (contacts == null || contacts.isEmpty()) {
                            showEmergencyOptionsDialog(
                                    message,
                                    latitude,
                                    longitude,
                                    new ArrayList<>()
                            );
                            return;
                        }

                        // Extract phone numbers and names
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

                            // Google Maps link
                            String locationUrl = String.format(
                                    Locale.getDefault(),
                                    "https://maps.google.com/?q=%f,%f",
                                    latitude,
                                    longitude
                            );

                            // SMS message
                            String smsMessage = String.format(
                                    Locale.getDefault(),
                                    "🚨 EMERGENCY SOS ALERT 🚨\n\n" +
                                            "User: %s\n" +
                                            "Location: %s\n\n" +
                                            "They need help! Please check on them immediately.",
                                    currentUserName,
                                    locationUrl
                            );

                            // Open SMS app
                            sosHelper.openSmsComposer(phoneNumbers, smsMessage);

                            // Show summary dialog
                            showEmergencyAlertSummary(contactNames, phoneNumbers.size());
                        }

                        // Show emergency options dialog
                        showEmergencyOptionsDialog(
                                message,
                                latitude,
                                longitude,
                                phoneNumbers
                        );
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                SosActivity.this,
                                "Could not load contacts. Check Firebase rules.",
                                Toast.LENGTH_SHORT
                        ).show();

                        showEmergencyOptionsDialog(
                                message,
                                latitude,
                                longitude,
                                new ArrayList<>()
                        );
                    }
                });
    }

    /**
     * Shows summary of emergency alerts sent
     */
    private void showEmergencyAlertSummary(
            List<String> contactNames,
            int contactCount
    ) {

        if (contactNames.isEmpty()) return;

        String contacts = android.text.TextUtils.join(", ", contactNames);

        String message = String.format(
                Locale.getDefault(),
                "SOS alert activated!\n\nAlerts sent to %d contact(s):\n%s\n\nLocation has been shared.",
                contactCount,
                contacts
        );

        new AlertDialog.Builder(this)
                .setTitle("🚨 SOS Activated")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Shows emergency options dialog (call emergency services)
     */
    private void showEmergencyOptionsDialog(
            String message,
            double latitude,
            double longitude,
            List<String> phoneNumbers
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Emergency SOS Activated")
                .setMessage(
                        phoneNumbers.isEmpty()
                                ? "No emergency contacts found.\n\nYour location has been shared.\n\nWould you like to call emergency services?"
                                : "SOS alerts sent to emergency contacts!\n\nYour location has been shared.\n\nWould you like to call emergency services?"
                )
                .setPositiveButton(
                        phoneNumbers.isEmpty() ? "Call 119" : "Call Emergency",
                        (dialog, which) -> sosHelper.openEmergencyDialer()
                )
                .setNegativeButton("Close", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Old method (deprecated)
     */
    @Deprecated
    private void promptEmergencyContactAlert(String message) {

    }

    /**
     * Permission result handler
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_LOCATION) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Retry SOS if permission granted
                activateSOS();

            } else {
                // Send SOS even without location
                sendSosWithCoordinates(0.0, 0.0);
            }
        }
    }

    /**
     * Cleanup timer
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}