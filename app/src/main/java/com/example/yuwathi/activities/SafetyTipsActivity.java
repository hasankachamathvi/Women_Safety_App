package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SafetyTipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        RecyclerView rvTips = findViewById(R.id.rv_tips);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setSelectedItemId(R.id.nav_tips);

        rvTips.setLayoutManager(new LinearLayoutManager(this));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SafetyTipsActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(SafetyTipsActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(SafetyTipsActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(SafetyTipsActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
