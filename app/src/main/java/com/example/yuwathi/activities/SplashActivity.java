package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.iv_logo);
        TextView title = findViewById(R.id.tv_app_name);

        if (logo != null) {
            logo.setAlpha(0f);
            logo.animate().alpha(1f).setDuration(800).start();
        }
        if (title != null) {
            title.setAlpha(0f);
            title.animate().alpha(1f).setDuration(800).setStartDelay(300).start();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 2500);
    }
}
