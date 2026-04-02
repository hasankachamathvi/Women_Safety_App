package com.example.yuwathi.services;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.yuwathi.models.User;

/**
 * Firebase Authentication Service
 * Handles user login, registration, and authentication
 */
public class FirebaseAuthService {
    private static final String TAG = "FirebaseAuthService";
    private FirebaseAuth mAuth;

    public FirebaseAuthService() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Register a new user with email and password
     */
    public void registerUser(String email, String password, String name, String phone, OnAuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Create user in Firestore
                            User user = new User(
                                    firebaseUser.getUid(),
                                    name,
                                    email,
                                    phone,
                                    "Active"
                            );
                            FirebaseFirestoreService.getInstance().addUser(user, new FirebaseFirestoreService.OnOperationCallback() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess("Registration successful");
                                }

                                @Override
                                public void onError(String error) {
                                    callback.onError(error != null ? error : "Failed to save user profile");
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Registration failed", task.getException());
                        String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        callback.onError(error);
                    }
                });
    }

    /**
     * Login user with email and password
     */
    public void loginUser(String email, String password, OnAuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Login successful for user: " + user.getEmail());
                            callback.onSuccess("Login successful");
                        }
                    } else {
                        Log.e(TAG, "Login failed", task.getException());
                        String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        callback.onError(error);
                    }
                });
    }

    /**
     * Logout current user
     */
    public void logout() {
        mAuth.signOut();
        Log.d(TAG, "User logged out");
    }

    /**
     * Get currently logged-in user
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Reset password for user
     */
    public void resetPassword(String email, OnAuthCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Password reset email sent");
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                        callback.onError(error);
                    }
                });
    }

    /**
     * Callback interface for authentication operations
     */
    public interface OnAuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}

