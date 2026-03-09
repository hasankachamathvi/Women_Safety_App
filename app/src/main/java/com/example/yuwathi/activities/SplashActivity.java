package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;

/**
 * SplashActivity - This is the first screen that appears when the app starts.
 * It shows the app name with a fade-in animation and automatically
 * navigates to the Login page after 2.5 seconds.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the splash screen layout
        setContentView(R.layout.activity_splash);

        // Find the title and subtitle text views
        TextView title = findViewById(R.id.tv_app_name);
        TextView subtitle = findViewById(R.id.tv_subtitle);

        // Fade-in animation for the app title (800ms duration)
        if (title != null) {
            title.setAlpha(0f);           // Start invisible
            title.animate().alpha(1f).setDuration(800).start();  // Fade to visible
        }
        
        // Fade-in animation for the subtitle (starts after 300ms delay)
        if (subtitle != null) {
            subtitle.setAlpha(0f);        // Start invisible
            subtitle.animate().alpha(1f).setDuration(800).setStartDelay(300).start();
        }

        // Wait 2.5 seconds then go to Login page
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();  // Close splash so user can't go back to it
        }, 2500);
    }
}
