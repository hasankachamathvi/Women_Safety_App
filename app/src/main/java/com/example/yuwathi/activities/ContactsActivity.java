//package com.example.yuwathi.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yuwathi.R;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.button.MaterialButton;
//
//public class ContactsActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contacts);
//
//        RecyclerView rvContacts = findViewById(R.id.rv_contacts);
//        MaterialButton btnAddContact = findViewById(R.id.btn_add_contact);
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
//
//        bottomNav.setSelectedItemId(R.id.nav_contacts);
//
//        rvContacts.setLayoutManager(new LinearLayoutManager(this));
//
//        btnAddContact.setOnClickListener(v ->
//                Toast.makeText(ContactsActivity.this, "Add contact dialog - coming soon!", Toast.LENGTH_SHORT).show()
//        );
//
//        bottomNav.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.nav_home) {
//                startActivity(new Intent(ContactsActivity.this, HomeActivity.class));
//                return true;
//            } else if (id == R.id.nav_contacts) {
//                return true;
//            } else if (id == R.id.nav_sos) {
//                startActivity(new Intent(ContactsActivity.this, SosActivity.class));
//                return true;
//            } else if (id == R.id.nav_tips) {
//                startActivity(new Intent(ContactsActivity.this, SafetyTipsActivity.class));
//                return true;
//            } else if (id == R.id.nav_profile) {
//                startActivity(new Intent(ContactsActivity.this, ProfileActivity.class));
//                return true;
//            }
//            return false;
//        });
//    }
//}
