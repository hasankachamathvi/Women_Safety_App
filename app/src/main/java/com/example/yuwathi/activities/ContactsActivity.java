package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the contacts screen layout
        setContentView(R.layout.activity_contacts);

        // Find views from the layout
        RecyclerView rvContacts = findViewById(R.id.rv_contacts);       // List of emergency contacts
        MaterialButton btnAddContact = findViewById(R.id.btn_add_contact); // Add new contact button
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);    // Bottom navigation bar

        // Set Contacts as the selected tab in bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_contacts);

        // Set up the contacts list with vertical scrolling layout
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        // Handle Add Contact button click
        btnAddContact.setOnClickListener(v ->
                Toast.makeText(ContactsActivity.this, "Add contact dialog - coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Handle bottom navigation bar item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ContactsActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                return true;  // Already on Contacts, do nothing
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(ContactsActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(ContactsActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ContactsActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
