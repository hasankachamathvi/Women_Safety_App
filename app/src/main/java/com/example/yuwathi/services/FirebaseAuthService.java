package com.example.yuwathi.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Wrapper around Firebase Authentication operations.
 */
public class FirebaseAuthService {
    private final FirebaseAuth auth;

    public FirebaseAuthService() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }
}

