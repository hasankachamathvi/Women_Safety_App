package com.example.yuwathi.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Wrapper around Firebase Authentication operations.
 */
public class FirebaseAuthService {
    private final FirebaseAuth auth;

    public interface OnAuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public FirebaseAuthService() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void loginUser(String email, String password, OnAuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onSuccess("Login successful"))
                .addOnFailureListener(e -> callback.onError(e.getMessage() != null ? e.getMessage() : "Login failed"));
    }

    public void resetPassword(String email, OnAuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Password reset email sent"))
                .addOnFailureListener(e -> callback.onError(e.getMessage() != null ? e.getMessage() : "Failed to send reset email"));
    }

    public void logout() {
        auth.signOut();
    }
}

