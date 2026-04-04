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
        setContentView(R.layout.activity_database_connection_test);
    }
}

