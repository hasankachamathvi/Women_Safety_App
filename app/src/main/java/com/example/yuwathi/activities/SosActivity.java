package com.example.yuwathi.activities;

// Import required Android classes
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class SosActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;  // Timer for the 3-second countdown
    private boolean isActivated = false;    // Flag to track if SOS is already activated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the SOS screen layout
        setContentView(R.layout.activity_sos);

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
        tvStatus.setText("Hold the button to send alerts");
    }

    private void activateSOS() {
        Toast.makeText(this, "SOS Alert Sent to Emergency Contacts!", Toast.LENGTH_LONG).show();
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
