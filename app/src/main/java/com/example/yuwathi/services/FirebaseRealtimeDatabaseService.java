package com.example.yuwathi.services;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Realtime Database service for live alerts and location updates.
 */
public class FirebaseRealtimeDatabaseService {
    private static final String TAG = "RealtimeDBService";
    private static FirebaseRealtimeDatabaseService instance;

    private final DatabaseReference reference;

    private FirebaseRealtimeDatabaseService() {
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseRealtimeDatabaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseRealtimeDatabaseService();
        }
        return instance;
    }

    public void sendSOSAlert(String userId, String userName, double latitude, double longitude,
                             String message, OnOperationCallback callback) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userName", userName);
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);
        payload.put("message", message);
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("status", "active");

        reference.child("sos_alerts").child(userId).setValue(payload)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "sendSOSAlert failed", e);
                    callback.onError(e.getMessage() == null ? "Unknown error" : e.getMessage());
                });
    }

    public void shareLocation(String userId, String userName, double latitude, double longitude,
                              OnOperationCallback callback) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userName", userName);
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);
        payload.put("timestamp", System.currentTimeMillis());

        reference.child("live_locations").child(userId).setValue(payload)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unknown error" : e.getMessage()));
    }

    public void stopLocationSharing(String userId, OnOperationCallback callback) {
        reference.child("live_locations").child(userId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unknown error" : e.getMessage()));
    }

    public void cancelSOSAlert(String userId, OnOperationCallback callback) {
        reference.child("sos_alerts").child(userId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unknown error" : e.getMessage()));
    }

    public void listenToSOSAlerts(ValueEventListener listener) {
        reference.child("sos_alerts").addValueEventListener(listener);
    }

    public void removeListener(String path, ValueEventListener listener) {
        reference.child(path).removeEventListener(listener);
    }

    public interface OnOperationCallback {
        void onSuccess();
        void onError(String error);
    }
}

