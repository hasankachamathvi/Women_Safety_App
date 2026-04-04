package com.example.yuwathi.activities;

// Android classes used for screen navigation and delayed execution.
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
// AppCompat base activity for backward-compatible activity behavior.
import androidx.appcompat.app.AppCompatActivity;
// App resources (layouts, view ids, etc.).
import com.example.yuwathi.R;

// Launch screen shown briefly before moving to login.
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity and load splash screen layout.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get splash text views for simple fade-in animation.
        TextView title = findViewById(R.id.tv_app_name);
        TextView subtitle = findViewById(R.id.tv_subtitle);

        // Fade in app title if the view exists in the layout.
        if (title != null) {
            // Start fully transparent.
            title.setAlpha(0f);
            // Animate to fully visible in 800 ms.
            title.animate().alpha(1f).setDuration(800).start();
        }
        
        // Fade in subtitle slightly after the title for a staggered effect.
        if (subtitle != null) {
            // Start fully transparent.
            subtitle.setAlpha(0f);
            // Fade in with a small delay to match title animation.
            subtitle.animate().alpha(1f).setDuration(800).setStartDelay(300).start();
        }

        // After splash delay, open login screen and close splash from back stack.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Navigate to login once splash display is complete.
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            // Apply a smooth fade transition between activities.
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            // Prevent returning to splash when the user presses back.
            finish();
        // Keep splash visible for 2 seconds.
        }, 2000);
    }
}
