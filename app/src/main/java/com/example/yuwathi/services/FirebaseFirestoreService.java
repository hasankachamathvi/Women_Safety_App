package com.example.yuwathi.services;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.example.yuwathi.models.User;
import com.example.yuwathi.models.SafetyTip;
import com.example.yuwathi.models.Complaint;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Firebase Firestore Service
 * Handles all database operations for users, complaints, safety tips, and contacts
 */
public class FirebaseFirestoreService {
    private static final String TAG = "FirestoreService";
    private static FirebaseFirestoreService instance;
    private FirebaseFirestore db;

    // Collection names
    private static final String USERS_COLLECTION = "users";
    private static final String COMPLAINTS_COLLECTION = "complaints";
    private static final String SAFETY_TIPS_COLLECTION = "safety_tips";
    private static final String CONTACTS_COLLECTION = "emergency_contacts";
    private static final String LOCATIONS_COLLECTION = "locations";

    private FirebaseFirestoreService() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestoreService getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreService();
        }
        return instance;
    }

    // ========== USER OPERATIONS ==========

    /**
     * Add or update user profile
     */
    public void addUser(User user, OnOperationCallback callback) {
        db.collection(USERS_COLLECTION).document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User added successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding user", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get user by ID
     */
    public void getUser(String userId, OnUserFetchCallback callback) {
        db.collection(USERS_COLLECTION).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onError("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get all users (for admin)
     */
    public void getAllUsers(OnUsersListCallback callback) {
        db.collection(USERS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = querySnapshot.toObjects(User.class);
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching users", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Update user profile
     */
    public void updateUser(String userId, Map<String, Object> updates, OnOperationCallback callback) {
        db.collection(USERS_COLLECTION).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User updated successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Delete user (admin only)
     */
    public void deleteUser(String userId, OnOperationCallback callback) {
        db.collection(USERS_COLLECTION).document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user", e);
                    callback.onError(e.getMessage());
                });
    }

    // ========== COMPLAINT OPERATIONS ==========

    /**
     * Submit a new complaint
     */
    public void submitComplaint(Complaint complaint, OnOperationCallback callback) {
        DocumentReference docRef = db.collection(COMPLAINTS_COLLECTION).document();
        complaint.setId(docRef.getId());
        docRef.set(complaint)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Complaint submitted with ID: " + docRef.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error submitting complaint", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get all complaints by user
     */
    public void getComplaintsByUser(String userId, OnComplaintsListCallback callback) {
        db.collection(COMPLAINTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Complaint> complaints = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Complaint complaint = doc.toObject(Complaint.class);
                        if (complaint != null) {
                            complaint.setId(doc.getId());
                            complaints.add(complaint);
                        }
                    }
                    callback.onSuccess(complaints);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching complaints", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get all complaints (for admin)
     */
    public void getAllComplaints(OnComplaintsListCallback callback) {
        db.collection(COMPLAINTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Complaint> complaints = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Complaint complaint = doc.toObject(Complaint.class);
                        if (complaint != null) {
                            complaint.setId(doc.getId());
                            complaints.add(complaint);
                        }
                    }
                    callback.onSuccess(complaints);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching complaints", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Update complaint status
     */
    public void updateComplaintStatus(String complaintId, String status, OnOperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", new Date());

        db.collection(COMPLAINTS_COLLECTION).document(complaintId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Complaint status updated");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating complaint", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Delete complaint (admin only)
     */
    public void deleteComplaint(String complaintId, OnOperationCallback callback) {
        db.collection(COMPLAINTS_COLLECTION).document(complaintId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Complaint deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting complaint", e);
                    callback.onError(e.getMessage());
                });
    }

    // ========== SAFETY TIPS OPERATIONS ==========

    /**
     * Get all safety tips (admin view, includes hidden tips)
     */
    public void getAllSafetyTips(OnSafetyTipsListCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SafetyTip> tips = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SafetyTip tip = doc.toObject(SafetyTip.class);
                        if (tip != null) {
                            tip.setId(doc.getId());
                            tips.add(tip);
                        }
                    }
                    callback.onSuccess(tips);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching all safety tips", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get all safety tips
     */
    public void getSafetyTips(OnSafetyTipsListCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION)
                .whereEqualTo("isVisible", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SafetyTip> tips = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SafetyTip tip = doc.toObject(SafetyTip.class);
                        if (tip != null) {
                            tip.setId(doc.getId());
                            tips.add(tip);
                        }
                    }
                    callback.onSuccess(tips);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching safety tips", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get all safety tips by category
     */
    public void getSafetyTipsByCategory(String category, OnSafetyTipsListCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION)
                .whereEqualTo("category", category)
                .whereEqualTo("isVisible", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SafetyTip> tips = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SafetyTip tip = doc.toObject(SafetyTip.class);
                        if (tip != null) {
                            tip.setId(doc.getId());
                            tips.add(tip);
                        }
                    }
                    callback.onSuccess(tips);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching safety tips", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Add safety tip (admin only)
     */
    public void addSafetyTip(SafetyTip tip, OnOperationCallback callback) {
        DocumentReference docRef = db.collection(SAFETY_TIPS_COLLECTION).document();
        tip.setId(docRef.getId());
        docRef.set(tip)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Safety tip added with ID: " + docRef.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding safety tip", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Update safety tip (admin only)
     */
    public void updateSafetyTip(String tipId, SafetyTip tip, OnOperationCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION).document(tipId)
                .set(tip)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Safety tip updated");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating safety tip", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Delete safety tip (admin only)
     */
    public void deleteSafetyTip(String tipId, OnOperationCallback callback) {
        db.collection(SAFETY_TIPS_COLLECTION).document(tipId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Safety tip deleted");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting safety tip", e);
                    callback.onError(e.getMessage());
                });
    }

    // ========== EMERGENCY CONTACTS OPERATIONS ==========

    /**
     * Add emergency contact for user
     */
    public void addEmergencyContact(String userId, Map<String, Object> contact, OnOperationCallback callback) {
        DocumentReference docRef = db.collection(USERS_COLLECTION).document(userId)
                .collection(CONTACTS_COLLECTION).document();
        contact.put("id", docRef.getId());
        docRef.set(contact)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Emergency contact added");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding emergency contact", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get emergency contacts for user
     */
    public void getEmergencyContacts(String userId, OnContactsListCallback callback) {
        db.collection(USERS_COLLECTION).document(userId)
                .collection(CONTACTS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> contacts = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> contact = doc.getData();
                        if (contact != null) {
                            contact.put("id", doc.getId());
                            contacts.add(contact);
                        }
                    }
                    callback.onSuccess(contacts);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching emergency contacts", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Delete emergency contact
     */
    public void deleteEmergencyContact(String userId, String contactId, OnOperationCallback callback) {
        db.collection(USERS_COLLECTION).document(userId)
                .collection(CONTACTS_COLLECTION).document(contactId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Emergency contact deleted");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting emergency contact", e);
                    callback.onError(e.getMessage());
                });
    }

    // ========== LOCATION OPERATIONS ==========

    /**
     * Save user location for emergency
     */
    public void saveLocation(String userId, double latitude, double longitude, OnOperationCallback callback) {
        Map<String, Object> location = new HashMap<>();
        location.put("userId", userId);
        location.put("latitude", latitude);
        location.put("longitude", longitude);
        location.put("timestamp", new Date());

        db.collection(LOCATIONS_COLLECTION).add(location)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Location saved");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving location", e);
                    callback.onError(e.getMessage());
                });
    }

    // ========== ADMIN STATISTICS ==========

    /**
     * Get dashboard statistics (admin)
     */
    public void getDashboardStats(OnStatsCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Get total users
        db.collection(USERS_COLLECTION).get()
                .addOnSuccessListener(usersSnapshot -> {
                    int totalUsers = (int) usersSnapshot.getDocuments().size();

                    // Get total complaints
                    db.collection(COMPLAINTS_COLLECTION).get()
                            .addOnSuccessListener(complaintsSnapshot -> {
                                int totalComplaints = (int) complaintsSnapshot.getDocuments().size();

                                // Get resolved complaints
                                db.collection(COMPLAINTS_COLLECTION)
                                        .whereEqualTo("status", "Resolved")
                                        .get()
                                        .addOnSuccessListener(resolvedSnapshot -> {
                                            int resolvedComplaints = (int) resolvedSnapshot.getDocuments().size();
                                            Map<String, Integer> stats = new HashMap<>();
                                            stats.put("totalUsers", totalUsers);
                                            stats.put("totalComplaints", totalComplaints);
                                            stats.put("resolvedComplaints", resolvedComplaints);
                                            stats.put("pendingComplaints", totalComplaints - resolvedComplaints);
                                            callback.onSuccess(stats);
                                        })
                                        .addOnFailureListener(e -> callback.onError(e.getMessage()));
                            })
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========== CALLBACKS ==========

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

    public interface OnSafetyTipsListCallback {
        void onSuccess(List<SafetyTip> tips);
        void onError(String error);
    }

    public interface OnContactsListCallback {
        void onSuccess(List<Map<String, Object>> contacts);
        void onError(String error);
    }

    public interface OnStatsCallback {
        void onSuccess(Map<String, Integer> stats);
        void onError(String error);
    }
}

