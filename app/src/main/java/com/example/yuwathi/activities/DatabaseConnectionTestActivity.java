package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yuwathi.R;
import com.example.yuwathi.utils.DatabaseConnectionTest;

/**
 * Database Connection Test Activity
 * Use this activity to verify all Firebase connections are working
 */
public class DatabaseConnectionTestActivity extends AppCompatActivity {

    private TextView tvResults;
    private Button btnTestFirestore;
    private Button btnTestAuth;
    private Button btnTestRealtimeDB;
    private Button btnTestAll;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_connection_test);

        // Set toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🔍 Database Connection Test");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        tvResults = findViewById(R.id.tv_results);
        btnTestFirestore = findViewById(R.id.btn_test_firestore);
        btnTestAuth = findViewById(R.id.btn_test_auth);
        btnTestRealtimeDB = findViewById(R.id.btn_test_realtime_db);
        btnTestAll = findViewById(R.id.btn_test_all);
        btnClear = findViewById(R.id.btn_clear);
    }

    private void setupClickListeners() {
        btnTestFirestore.setOnClickListener(v -> testFirestore());
        btnTestAuth.setOnClickListener(v -> testAuth());
        btnTestRealtimeDB.setOnClickListener(v -> testRealtimeDB());
        btnTestAll.setOnClickListener(v -> testAll());
        btnClear.setOnClickListener(v -> clearResults());
    }

    private void testFirestore() {
        appendResult("🔍 Testing Firestore Connection...\n");
        DatabaseConnectionTest.testFirestoreConnection(new DatabaseConnectionTest.OnConnectionTestCallback() {
            @Override
            public void onSuccess(String message) {
                appendResult(message + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "✅ Firestore OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                appendResult("❌ FAILED: " + error + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "❌ Firestore Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarning(String message) {
                appendResult("⚠️ " + message + "\n");
            }
        });
    }

    private void testAuth() {
        appendResult("🔍 Testing Firebase Authentication...\n");
        DatabaseConnectionTest.testAuthConnection(new DatabaseConnectionTest.OnConnectionTestCallback() {
            @Override
            public void onSuccess(String message) {
                appendResult(message + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "✅ Auth OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                appendResult("❌ FAILED: " + error + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "❌ Auth Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarning(String message) {
                appendResult("⚠️ " + message + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "⚠️ " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void testRealtimeDB() {
        appendResult("🔍 Testing Firebase Realtime Database...\n");
        DatabaseConnectionTest.testRealtimeDatabaseConnection(new DatabaseConnectionTest.OnConnectionTestCallback() {
            @Override
            public void onSuccess(String message) {
                appendResult(message + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "✅ Realtime DB OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                appendResult("❌ FAILED: " + error + "\n");
                Toast.makeText(DatabaseConnectionTestActivity.this, "❌ Realtime DB Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarning(String message) {
                appendResult("⚠️ " + message + "\n");
            }
        });
    }

    private void testAll() {
        clearResults();
        appendResult("🚀 Running Complete Database Connection Test...\n");
        appendResult("====================================\n\n");

        DatabaseConnectionTest.runCompleteConnectionTest(new DatabaseConnectionTest.OnCompleteTestCallback() {
            @Override
            public void onTestsComplete(String results, boolean allPassed) {
                appendResult(results);
                if (allPassed) {
                    Toast.makeText(DatabaseConnectionTestActivity.this, "✅ ALL TESTS PASSED", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DatabaseConnectionTestActivity.this, "❌ SOME TESTS FAILED", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void appendResult(String text) {
        tvResults.append(text);
        // Auto-scroll to bottom
        ScrollView sv = findViewById(R.id.sv_results);
        sv.post(() -> sv.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void clearResults() {
        tvResults.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

