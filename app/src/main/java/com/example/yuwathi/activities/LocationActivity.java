//package com.example.yuwathi.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yuwathi.R;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.button.MaterialButton;
//
//public class LocationActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_location);
//
//        MaterialButton btnShare = findViewById(R.id.btn_share_location_action);
//        MaterialButton btnStop = findViewById(R.id.btn_stop_sharing);
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
//
//        btnShare.setOnClickListener(v ->
//                Toast.makeText(LocationActivity.this, "Live location sharing started!", Toast.LENGTH_SHORT).show()
//        );
//
//        btnStop.setOnClickListener(v ->
//                Toast.makeText(LocationActivity.this, "Location sharing stopped.", Toast.LENGTH_SHORT).show()
//        );
//
//        bottomNav.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.nav_home) {
//                startActivity(new Intent(LocationActivity.this, HomeActivity.class));
//                return true;
//            } else if (id == R.id.nav_contacts) {
//                startActivity(new Intent(LocationActivity.this, ContactsActivity.class));
//                return true;
//            } else if (id == R.id.nav_sos) {
//                startActivity(new Intent(LocationActivity.this, SosActivity.class));
//                return true;
//            } else if (id == R.id.nav_tips) {
//                startActivity(new Intent(LocationActivity.this, SafetyTipsActivity.class));
//                return true;
//            } else if (id == R.id.nav_profile) {
//                startActivity(new Intent(LocationActivity.this, ProfileActivity.class));
//                return true;
//            }
//            return false;
//        });
//    }
//}
