package com.example.yuwathi.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;

/**
 * Utility screen to verify backend connectivity.
 */
public class DatabaseConnectionTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Simple harness screen used during manual checks for Firebase connectivity/UI wiring.
        setContentView(R.layout.activity_database_connection_test);
    }
}
