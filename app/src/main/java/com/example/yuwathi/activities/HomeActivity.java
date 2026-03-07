package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialButton btnSos = findViewById(R.id.btn_sos);
        LinearLayout btnShareLoc = findViewById(R.id.btn_share_location);
        LinearLayout btnContacts = findViewById(R.id.btn_contacts);
        LinearLayout btnReport = findViewById(R.id.btn_report);
        LinearLayout cardTip = findViewById(R.id.card_safety_tip);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setSelectedItemId(R.id.nav_home);

        btnSos.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SosActivity.class))
        );

        btnShareLoc.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, LocationActivity.class))
        );

        btnContacts.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ContactsActivity.class))
        );

        btnReport.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ComplaintActivity.class))
        );

        cardTip.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SafetyTipsActivity.class))
        );

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(HomeActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(HomeActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(HomeActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
