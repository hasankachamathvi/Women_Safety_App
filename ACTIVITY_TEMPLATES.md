# Backend Implementation - Remaining Activities Checklist

## Quick Copy-Paste Templates for Each Activity

### 1. SafetyTipsActivity
```java
package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.example.yuwathi.adapters.SafetyTipAdapter;
import com.example.yuwathi.models.SafetyTip;
import java.util.ArrayList;
import java.util.List;

public class SafetyTipsActivity extends AppCompatActivity {
    private FirebaseFirestoreService firestoreService;
    private RecyclerView recyclerView;
    private SafetyTipAdapter adapter;
    private List<SafetyTip> tipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_tips);

        firestoreService = FirebaseFirestoreService.getInstance();
        recyclerView = findViewById(R.id.recycler_tips);
        tipList = new ArrayList<>();
        adapter = new SafetyTipAdapter(this, tipList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadSafetyTips();
    }

    private void loadSafetyTips() {
        firestoreService.getSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
            @Override
            public void onSuccess(List<SafetyTip> tips) {
                tipList.clear();
                tipList.addAll(tips);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SafetyTipsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

### 2. ContactsActivity
```java
package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {
    private FirebaseFirestoreService firestoreService;
    private String currentUserId;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        firestoreService = FirebaseFirestoreService.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        recyclerView = findViewById(R.id.recycler_contacts);
        contactsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadEmergencyContacts();
    }

    private void loadEmergencyContacts() {
        firestoreService.getEmergencyContacts(currentUserId, 
            new FirebaseFirestoreService.OnContactsListCallback() {
                @Override
                public void onSuccess(List<Map<String, Object>> contacts) {
                    contactsList.clear();
                    contactsList.addAll(contacts);
                    // Update adapter
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ContactsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void addEmergencyContact(String name, String phone) {
        Map<String, Object> contact = new HashMap<>();
        contact.put("name", name);
        contact.put("phone", phone);
        contact.put("relationship", "Friend");

        firestoreService.addEmergencyContact(currentUserId, contact, 
            new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ContactsActivity.this, "Contact added", Toast.LENGTH_SHORT).show();
                    loadEmergencyContacts();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ContactsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
```

### 3. SosActivity
```java
package com.example.yuwathi.activities;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseRealtimeDatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class SosActivity extends AppCompatActivity {
    private FirebaseRealtimeDatabaseService realtimeService;
    private String currentUserId;
    private String currentUserName;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        realtimeService = FirebaseRealtimeDatabaseService.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserName = "User Name"; // Load from Firestore

        // TODO: Get GPS location
        latitude = 6.9271;
        longitude = 80.7744;

        findViewById(R.id.btn_sos).setOnLongClickListener(v -> {
            sendSOS();
            return true;
        });
    }

    private void sendSOS() {
        realtimeService.sendSOSAlert(currentUserId, currentUserName, 
            latitude, longitude, "Emergency assistance needed",
            new FirebaseRealtimeDatabaseService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(SosActivity.this, "SOS sent!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SosActivity.this, "SOS failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
```

### 4. ComplaintActivity
```java
package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yuwathi.R;
import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Date;

public class ComplaintActivity extends AppCompatActivity {
    private FirebaseFirestoreService firestoreService;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        firestoreService = FirebaseFirestoreService.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViewById(R.id.btn_submit).setOnClickListener(v -> submitComplaint());
    }

    private void submitComplaint() {
        EditText etCategory = findViewById(R.id.et_category);
        EditText etDescription = findViewById(R.id.et_description);

        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (category.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Complaint complaint = new Complaint();
        complaint.setUserId(currentUserId);
        complaint.setCategory(category);
        complaint.setDescription(description);
        complaint.setStatus("Pending");
        complaint.setTimestamp(new Date());

        firestoreService.submitComplaint(complaint, 
            new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ComplaintActivity.this, "Complaint submitted", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ComplaintActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
```

### 5. ProfileActivity
```java
package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUserProfile();
        
        findViewById(R.id.btn_save).setOnClickListener(v -> updateProfile());
        findViewById(R.id.btn_logout).setOnClickListener(v -> logout());
    }

    private void loadUserProfile() {
        firestoreService.getUser(currentUserId, new FirebaseFirestoreService.OnUserFetchCallback() {
            @Override
            public void onSuccess(com.example.yuwathi.models.User user) {
                EditText etName = findViewById(R.id.et_name);
                EditText etEmail = findViewById(R.id.et_email);
                EditText etPhone = findViewById(R.id.et_phone);

                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhone());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        EditText etName = findViewById(R.id.et_name);
        EditText etPhone = findViewById(R.id.et_phone);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString());
        updates.put("phone", etPhone.getText().toString());

        firestoreService.updateUser(currentUserId, updates, 
            new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void logout() {
        authService.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
```

### 6. AdminComplaintsActivity
```java
package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.example.yuwathi.adapters.ComplaintAdapter;
import com.example.yuwathi.models.Complaint;
import java.util.ArrayList;
import java.util.List;

public class AdminComplaintsActivity extends BaseAdminActivity {
    private FirebaseFirestoreService firestoreService;
    private RecyclerView recyclerView;
    private ComplaintAdapter adapter;
    private List<Complaint> complaintList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        firestoreService = FirebaseFirestoreService.getInstance();
        recyclerView = findViewById(R.id.recycler_complaints);
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapter(this, complaintList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadComplaints();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_complaints;
    }

    private void loadComplaints() {
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {
                complaintList.clear();
                complaintList.addAll(complaints);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminComplaintsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

---

## Quick Setup Steps

1. **Copy each template** to your project
2. **Update import statements** as needed
3. **Create layout files** if missing (activity_safety_tips.xml, activity_contacts.xml, etc.)
4. **Add Firebase service calls** matching your UI
5. **Test with real Firebase data**

## Common Patterns

### Load Data Pattern
```java
service.getData(callback with onSuccess and onError);
// Update UI on success
// Show toast on error
```

### Submit Data Pattern
```java
service.submitData(object, callback with onSuccess and onError);
// Show success message
// Refresh list or navigate away
```

### Update Data Pattern
```java
Map<String, Object> updates = new HashMap<>();
updates.put("field", "value");
service.updateData(id, updates, callback);
```

---

## Testing Checklist

- [ ] Register new user
- [ ] Login with registered user
- [ ] View user profile
- [ ] Update profile information
- [ ] Submit complaint
- [ ] View submitted complaints
- [ ] Send SOS alert
- [ ] Share location
- [ ] Add emergency contact
- [ ] View safety tips
- [ ] Admin: View all users
- [ ] Admin: Delete user
- [ ] Admin: View complaints
- [ ] Admin: Update complaint status
- [ ] Admin: View dashboard statistics
- [ ] Logout functionality


