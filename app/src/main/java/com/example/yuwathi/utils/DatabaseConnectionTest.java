package com.example.yuwathi.utils;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Connection Test Utility
 * Use this class to verify Firebase connections are working properly
 */
public class DatabaseConnectionTest {
    private static final String TAG = "DBConnectionTest";

    /**
     * Test Firestore Connection
     * Call this method to test if Firestore is properly connected
     */
    public static void testFirestoreConnection(OnConnectionTestCallback callback) {
        Log.d(TAG, "🔍 Testing Firestore Connection...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Try to write a test document
        Map<String, Object> testData = new HashMap<>();
        testData.put("testStatus", "connected");
        testData.put("timestamp", System.currentTimeMillis());
        testData.put("message", "Connection test - can be deleted");

        db.collection("connection_test")
                .document("test_doc_" + System.currentTimeMillis())
                .set(testData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Firestore Write Test PASSED");

                    // Now test read
                    testFirestoreRead(callback);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Firestore Write Test FAILED: " + e.getMessage());
                    callback.onFailure("Firestore Write Failed: " + e.getMessage());
                });
    }

    /**
     * Test Firestore Read Operation
     */
    private static void testFirestoreRead(OnConnectionTestCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("connection_test")
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "✅ Firestore Read Test PASSED");
                    Log.d(TAG, "   Retrieved " + querySnapshot.size() + " document(s)");
                    callback.onSuccess("✅ Firestore Connection: OK");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Firestore Read Test FAILED: " + e.getMessage());
                    callback.onFailure("Firestore Read Failed: " + e.getMessage());
                });
    }

    /**
     * Test Firebase Authentication
     */
    public static void testAuthConnection(OnConnectionTestCallback callback) {
        Log.d(TAG, "🔍 Testing Firebase Authentication...");

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                Log.d(TAG, "✅ Firebase Auth Connected");
                Log.d(TAG, "   Current User: " + currentUser.getEmail());
                Log.d(TAG, "   User ID: " + currentUser.getUid());
                callback.onSuccess("✅ Firebase Auth: OK (User: " + currentUser.getEmail() + ")");
            } else {
                Log.w(TAG, "⚠️ Firebase Auth: No user logged in");
                callback.onWarning("⚠️ No user currently logged in. Please log in first.");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Firebase Auth Test FAILED: " + e.getMessage());
            callback.onFailure("Auth Test Failed: " + e.getMessage());
        }
    }

    /**
     * Test Firebase Realtime Database Connection
     */
    public static void testRealtimeDatabaseConnection(OnConnectionTestCallback callback) {
        Log.d(TAG, "🔍 Testing Firebase Realtime Database...");

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("connection_test")
                    .setValue("test_" + System.currentTimeMillis())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "✅ Realtime DB Write Test PASSED");
                        callback.onSuccess("✅ Realtime DB Connection: OK");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Realtime DB Test FAILED: " + e.getMessage());
                        callback.onFailure("Realtime DB Failed: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "❌ Realtime DB Test FAILED: " + e.getMessage());
            callback.onFailure("Realtime DB Test Failed: " + e.getMessage());
        }
    }

    /**
     * Comprehensive Connection Test - Tests all services
     */
    public static void runCompleteConnectionTest(OnCompleteTestCallback callback) {
        Log.d(TAG, "🚀 Running Complete Database Connection Test...");
        Log.d(TAG, "================================");

        StringBuilder results = new StringBuilder();
        results.append("DATABASE CONNECTION TEST RESULTS\n");
        results.append("================================\n\n");

        // Test 1: Authentication
        testAuthConnection(new OnConnectionTestCallback() {
            @Override
            public void onSuccess(String message) {
                results.append("1. ").append(message).append("\n");
                Log.d(TAG, message);

                // Test 2: Firestore
                testFirestoreConnection(new OnConnectionTestCallback() {
                    @Override
                    public void onSuccess(String message) {
                        results.append("2. ").append(message).append("\n");
                        Log.d(TAG, message);

                        // Test 3: Realtime DB
                        testRealtimeDatabaseConnection(new OnConnectionTestCallback() {
                            @Override
                            public void onSuccess(String message) {
                                results.append("3. ").append(message).append("\n");
                                Log.d(TAG, message);
                                results.append("\n✅ ALL TESTS PASSED!");
                                callback.onTestsComplete(results.toString(), true);
                            }

                            @Override
                            public void onFailure(String error) {
                                results.append("3. ❌ Realtime DB: FAILED\n");
                                results.append("   Error: ").append(error).append("\n");
                                Log.e(TAG, error);
                                callback.onTestsComplete(results.toString(), false);
                            }

                            @Override
                            public void onWarning(String message) {
                                results.append("3. ⚠️ Realtime DB: ").append(message).append("\n");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        results.append("2. ❌ Firestore: FAILED\n");
                        results.append("   Error: ").append(error).append("\n");
                        Log.e(TAG, error);
                        callback.onTestsComplete(results.toString(), false);
                    }

                    @Override
                    public void onWarning(String message) {
                        results.append("2. ⚠️ Firestore: ").append(message).append("\n");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                results.append("1. ❌ Auth: FAILED\n");
                results.append("   Error: ").append(error).append("\n");
                Log.e(TAG, error);
                callback.onTestsComplete(results.toString(), false);
            }

            @Override
            public void onWarning(String message) {
                results.append("1. ⚠️ Auth: ").append(message).append("\n");
                callback.onTestsComplete(results.toString(), false);
            }
        });
    }

    /**
     * Callback for single connection test
     */
    public interface OnConnectionTestCallback {
        void onSuccess(String message);
        void onFailure(String error);
        void onWarning(String message);
    }

    /**
     * Callback for complete connection test
     */
    public interface OnCompleteTestCallback {
        void onTestsComplete(String results, boolean allPassed);
    }
}

