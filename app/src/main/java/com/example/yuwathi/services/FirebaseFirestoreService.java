package com.example.yuwathi.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.models.SafetyTip;
import com.example.yuwathi.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized Firestore data access layer for app features.
 */
public class FirebaseFirestoreService {
    private static final String TAG = "FirestoreService";
    private static final String USERS_COLLECTION = "users";
    private static final String CONTACTS_COLLECTION = "emergency_contacts";
    private static final String COMPLAINTS_COLLECTION = "complaints";
    private static final String SAFETY_TIPS_COLLECTION = "safety_tips";
    private static final String LOCATIONS_COLLECTION = "locations";

    private static FirebaseFirestoreService instance;
    private final FirebaseFirestore db;

    private FirebaseFirestoreService() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestoreService getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreService();
        }
        return instance;
    }

    public void getUser(String userId, OnUserFetchCallback callback) {
        db.collection(USERS_COLLECTION).document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        callback.onError("User not found");
                        return;
                    }
                    User user = mapUser(snapshot);
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void updateUser(String userId, Map<String, Object> updates, OnOperationCallback callback) {
        db.collection(USERS_COLLECTION).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void addEmergencyContact(String userId, Map<String, Object> contact, OnOperationCallback callback) {
        DocumentReference ref = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .document();
        contact.put("id", ref.getId());
        ref.set(contact)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void updateEmergencyContact(String userId, String contactId, Map<String, Object> contact, OnOperationCallback callback) {
        if (contactId == null || contactId.trim().isEmpty()) {
            callback.onError("Missing contact ID");
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        if (contact != null) {
            payload.putAll(contact);
        }
        payload.put("id", contactId);

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .document(contactId)
                .set(payload)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void getEmergencyContacts(String userId, OnContactsListCallback callback) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> contacts = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> data = doc.getData();
                        if (data == null) {
                            data = new HashMap<>();
                        }
                        data.put("id", doc.getId());
                        contacts.add(data);
                    }
                    callback.onSuccess(contacts);
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void deleteEmergencyContact(String userId, String contactId, OnOperationCallback callback) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CONTACTS_COLLECTION)
                .document(contactId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void saveLocation(String userId, double latitude, double longitude, OnOperationCallback callback) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("latitude", latitude);
        payload.put("longitude", longitude);
        payload.put("timestamp", new Date());

        db.collection(LOCATIONS_COLLECTION)
                .add(payload)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void submitComplaint(Complaint complaint, OnOperationCallback callback) {
        DocumentReference ref = db.collection(COMPLAINTS_COLLECTION).document();
        complaint.setId(ref.getId());
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", complaint.getId());
        payload.put("userId", complaint.getUserId());
        payload.put("title", complaint.getTitle());
        payload.put("location", complaint.getLocation());
        payload.put("date", complaint.getDate());
        payload.put("status", complaint.getStatus());
        payload.put("priority", complaint.getPriority());
        payload.put("description", complaint.getDescription());
        payload.put("witnesses", complaint.getWitnesses());
        payload.put("vehicle", complaint.getVehicle());
        payload.put("suspectDescription", complaint.getSuspectDescription());
        payload.put("ongoing", complaint.isOngoing());
        payload.put("contactPreference", complaint.getContactPreference());
        payload.put("evidence", complaint.getEvidence());
        payload.put("timestamp", new Date());

        ref.set(payload)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void getSafetyTips(OnSafetyTipsListCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SafetyTip> tips = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SafetyTip tip = doc.toObject(SafetyTip.class);
                        if (tip == null) {
                            continue;
                        }
                        tip.setId(doc.getId());

                        Boolean visible = doc.getBoolean("visible");
                        Boolean legacyVisible = doc.getBoolean("isVisible");
                        boolean shouldShow = (visible != null && visible) || (legacyVisible != null && legacyVisible);

                        // Backward compatibility: if visibility field is missing, show tip by default.
                        if (visible == null && legacyVisible == null) {
                            shouldShow = true;
                        }

                        if (shouldShow) {
                            tips.add(tip);
                        }
                    }
                    callback.onSuccess(tips);
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void getAllSafetyTips(OnSafetyTipsListCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SafetyTip> tips = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SafetyTip tip = doc.toObject(SafetyTip.class);
                        if (tip != null) {
                            tip.setId(doc.getId());
                            tips.add(tip);
                        }
                    }
                    callback.onSuccess(tips);
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void addSafetyTip(SafetyTip tip, OnOperationCallback callback) {
        DocumentReference ref = db.collection(SAFETY_TIPS_COLLECTION).document();
        tip.setId(ref.getId());
        ref.set(tip)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void updateSafetyTip(String tipId, SafetyTip tip, OnOperationCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION).document(tipId)
                .set(tip)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void deleteSafetyTip(String tipId, OnOperationCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION).document(tipId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void getAllUsers(OnUsersListCallback callback) {
        db.collection(USERS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        User user = mapUser(doc);
                        users.add(user);
                    }
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void getAllComplaints(OnComplaintsListCallback callback) {
        db.collection(COMPLAINTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Complaint> complaints = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Complaint complaint = doc.toObject(Complaint.class);
                        if (complaint != null) {
                            complaint.setId(doc.getId());
                            complaints.add(complaint);
                        }
                    }
                    callback.onSuccess(complaints);
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void updateComplaintStatus(String complaintId, String status, OnOperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", new Date());

        db.collection(COMPLAINTS_COLLECTION).document(complaintId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    public void isUserAdmin(String userId, OnAdminCheckCallback callback) {
        getUser(userId, new OnUserFetchCallback() {
            @Override
            public void onSuccess(User user) {
                boolean isAdmin = user != null && "admin".equalsIgnoreCase(user.getRole());
                callback.onResult(isAdmin);
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "Admin check failed: " + error);
                callback.onResult(false);
            }
        });
    }

    public void getDashboardStats(OnStatsCallback callback) {
        db.collection(USERS_COLLECTION).get()
                .addOnSuccessListener(users -> {
                    int totalUsers = users.size();
                    db.collection(COMPLAINTS_COLLECTION).get()
                            .addOnSuccessListener(complaints -> {
                                int totalComplaints = complaints.size();
                                db.collection(COMPLAINTS_COLLECTION)
                                        .whereEqualTo("status", "Resolved")
                                        .get()
                                        .addOnSuccessListener(resolved -> {
                                            Map<String, Integer> stats = new HashMap<>();
                                            stats.put("totalUsers", totalUsers);
                                            stats.put("totalComplaints", totalComplaints);
                                            stats.put("resolvedComplaints", resolved.size());
                                            stats.put("pendingComplaints", Math.max(totalComplaints - resolved.size(), 0));
                                            callback.onSuccess(stats);
                                        })
                                        .addOnFailureListener(e -> callback.onError(messageOf(e)));
                            })
                            .addOnFailureListener(e -> callback.onError(messageOf(e)));
                })
                .addOnFailureListener(e -> callback.onError(messageOf(e)));
    }

    private User mapUser(@NonNull DocumentSnapshot snapshot) {
        User user = snapshot.toObject(User.class);
        if (user == null) {
            user = new User();
        }
        user.setId(snapshot.getId());

        Object fullName = snapshot.get("fullName");
        Object name = snapshot.get("name");
        Object username = snapshot.get("username");
        Object email = snapshot.get("email");
        Object phone = snapshot.get("phone");
        Object role = snapshot.get("role");
        Object status = snapshot.get("status");

        if (name instanceof String) user.setName((String) name);
        if (fullName instanceof String) user.setFullName((String) fullName);
        if (username instanceof String) user.setUsername((String) username);
        if (email instanceof String) user.setEmail((String) email);
        if (phone instanceof String) user.setPhone((String) phone);
        if (role instanceof String) user.setRole((String) role);
        if (status instanceof String) user.setStatus((String) status);

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        return user;
    }

    private String messageOf(Exception e) {
        return e != null && e.getMessage() != null ? e.getMessage() : "Unknown error";
    }

    public interface OnOperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface OnUserFetchCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface OnUsersListCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    public interface OnComplaintsListCallback {
        void onSuccess(List<Complaint> complaints);
        void onError(String error);
    }

    public interface OnContactsListCallback {
        void onSuccess(List<Map<String, Object>> contacts);
        void onError(String error);
    }

    public interface OnSafetyTipsListCallback {
        void onSuccess(List<SafetyTip> tips);
        void onError(String error);
    }

    public interface OnStatsCallback {
        void onSuccess(Map<String, Integer> stats);
        void onError(String error);
    }

    public interface OnAdminCheckCallback {
        void onResult(boolean isAdmin);
    }
}

