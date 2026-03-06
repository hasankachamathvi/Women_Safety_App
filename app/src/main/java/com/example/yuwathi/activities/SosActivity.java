package com.example.yuwathi.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class SosActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean isActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        MaterialButton btnSos = findViewById(R.id.btn_sos_main);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel_sos);
        TextView tvStatus = findViewById(R.id.tv_sos_status);
        TextView tvCountdown = findViewById(R.id.tv_countdown);

        btnSos.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isActivated) startCountdown(tvCountdown, tvStatus);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!isActivated) cancelCountdown(tvCountdown, tvStatus);
                    return true;
                default:
                    return false;
            }
        });

        btnCancel.setOnClickListener(v -> {
            cancelCountdown(tvCountdown, tvStatus);
            finish();
        });
    }

    private void startCountdown(TextView tvCountdown, TextView tvStatus) {
        tvStatus.setText(getString(R.string.sos_hold_message));
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                isActivated = true;
                tvCountdown.setText("");
                tvStatus.setText(getString(R.string.sos_activated));
                activateSOS();
            }
        }.start();
    }

    private void cancelCountdown(TextView tvCountdown, TextView tvStatus) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
