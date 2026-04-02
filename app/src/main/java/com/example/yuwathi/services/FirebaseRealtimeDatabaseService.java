package com.example.yuwathi.services;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Firebase Realtime Database Service
 * Handles real-time location sharing and SOS alerts
 */
public class FirebaseRealtimeDatabaseService {
    private static final String TAG = "RealtimeDBService";
    private static FirebaseRealtimeDatabaseService instance;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private FirebaseRealtimeDatabaseService() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    public static FirebaseRealtimeDatabaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseRealtimeDatabaseService();
        }
        return instance;
    }

    /**
     * Send SOS alert with location
     */
    public void sendSOSAlert(String userId, String userName, double latitude, double longitude,
                            String message, OnOperationCallback callback) {
        Map<String, Object> sosAlert = new HashMap<>();
        sosAlert.put("userId", userId);
        sosAlert.put("userName", userName);
        sosAlert.put("latitude", latitude);
        sosAlert.put("longitude", longitude);
        sosAlert.put("message", message);
        sosAlert.put("timestamp", System.currentTimeMillis());
        sosAlert.put("status", "active");

        reference.child("sos_alerts").child(userId).setValue(sosAlert)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "SOS alert sent successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending SOS alert", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Share live location
     */
    public void shareLocation(String userId, String userName, double latitude, double longitude,
                             OnOperationCallback callback) {
        Map<String, Object> location = new HashMap<>();
        location.put("userId", userId);
        location.put("userName", userName);
        location.put("latitude", latitude);
        location.put("longitude", longitude);
        location.put("timestamp", System.currentTimeMillis());

        reference.child("live_locations").child(userId).setValue(location)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location shared successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sharing location", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Stop sharing location
     */
    public void stopLocationSharing(String userId, OnOperationCallback callback) {
        reference.child("live_locations").child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location sharing stopped");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error stopping location sharing", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Cancel SOS alert
     */
    public void cancelSOSAlert(String userId, OnOperationCallback callback) {
        reference.child("sos_alerts").child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "SOS alert cancelled");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error cancelling SOS alert", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Listen for live location updates
     */
    public void listenToLocationUpdates(String userId, ValueEventListener listener) {
        reference.child("live_locations").child(userId).addValueEventListener(listener);
    }

    /**
     * Listen to all active SOS alerts (for emergency contacts/admin)
     */
    public void listenToSOSAlerts(ValueEventListener listener) {
        reference.child("sos_alerts").addValueEventListener(listener);
    }

    /**
     * Remove listener
     */
    public void removeListener(String path, ValueEventListener listener) {
        reference.child(path).removeEventListener(listener);
    }

    public interface OnOperationCallback {
        void onSuccess();
        void onError(String error);
    }
}

