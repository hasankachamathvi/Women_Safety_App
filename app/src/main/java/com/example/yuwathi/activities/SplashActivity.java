package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yuwathi.R;

/**
 * App entry splash screen that shows branding before navigation.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView title = findViewById(R.id.tv_app_name);
        TextView subtitle = findViewById(R.id.tv_subtitle);

        // Run lightweight fade-in animations to make the brand screen feel intentional,
        // while keeping total startup delay short.
        if (title != null) {
            title.setAlpha(0f);
            title.animate().alpha(1f).setDuration(800).start();
        }

        if (subtitle != null) {
            subtitle.setAlpha(0f);
            subtitle.animate().alpha(1f).setDuration(800).setStartDelay(300).start();
        }

        // Splash lasts only long enough to show branding; authentication/role checks happen on login.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000);
    }
}
