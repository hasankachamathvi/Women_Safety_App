package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout btnEditProfile = findViewById(R.id.btn_edit_profile);
        LinearLayout btnContacts = findViewById(R.id.btn_emergency_contacts);
        LinearLayout btnTips = findViewById(R.id.btn_tips);
        LinearLayout btnLogout = findViewById(R.id.btn_logout);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setSelectedItemId(R.id.nav_profile);

        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(ProfileActivity.this, "Edit Profile - coming soon!", Toast.LENGTH_SHORT).show()
        );

        btnContacts.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ContactsActivity.class))
        );

        btnTips.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, SafetyTipsActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(ProfileActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(ProfileActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(ProfileActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}
