import os

base = r"D:\2nd year\Mobile\APP\Women_Safety_App\app\src\main\java\com\example\yuwathi"
activities_dir = os.path.join(base, "activities")
utils_dir = os.path.join(base, "utils")

os.makedirs(activities_dir, exist_ok=True)
os.makedirs(utils_dir, exist_ok=True)

files = {}

files[os.path.join(activities_dir, "LoginActivity.java")] = r'''package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        TextView tvRegister = findViewById(R.id.tv_register_link);
        TextView tvForgot = findViewById(R.id.tv_forgot_password);
        ImageView ivToggle = findViewById(R.id.iv_toggle_password);

        ivToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(
                    isPasswordVisible ? null : PasswordTransformationMethod.getInstance()
            );
            etPassword.setSelection(etPassword.getText().length());
            ivToggle.setImageResource(
                    isPasswordVisible
                            ? android.R.drawable.ic_menu_close_clear_cancel
                            : android.R.drawable.ic_menu_view
            );
        });

        btnSignIn.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        tvForgot.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Password reset link sent!", Toast.LENGTH_SHORT).show()
        );
    }
}
'''

files[os.path.join(activities_dir, "RegisterActivity.java")] = r'''package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private EditText etFullName;
    private EditText etContact;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ImageView ivShowPassword;
    private ImageView ivShowConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.et_full_name);
        etContact = findViewById(R.id.et_contact);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        ivShowPassword = findViewById(R.id.iv_show_password);
        ivShowConfirmPassword = findViewById(R.id.iv_show_confirm_password);

        MaterialButton btnRegister = findViewById(R.id.btn_register);
        TextView tvLoginLink = findViewById(R.id.tv_login_link);

        setupListeners(btnRegister, tvLoginLink);
    }

    private void setupListeners(MaterialButton btnRegister, TextView tvLoginLink) {
        btnRegister.setOnClickListener(v -> handleRegistration());

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        ivShowPassword.setOnClickListener(v -> togglePasswordVisibility());

        ivShowConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());
    }

    private void handleRegistration() {
        String contact = etContact.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (contact.isEmpty() || fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6 || !password.matches(".*\\d.*")) {
            etPassword.setError("Must contain a number and least of 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(null);
            ivShowPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivShowPassword.setImageResource(android.R.drawable.ic_menu_view);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        if (isConfirmPasswordVisible) {
            etConfirmPassword.setTransformationMethod(null);
            ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        } else {
            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
        }
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }
}
'''

files[os.path.join(activities_dir, "HomeActivity.java")] = r'''package com.example.yuwathi.activities;

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
'''

files[os.path.join(activities_dir, "LocationActivity.java")] = r'''package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        MaterialButton btnShare = findViewById(R.id.btn_share_location_action);
        MaterialButton btnStop = findViewById(R.id.btn_stop_sharing);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        btnShare.setOnClickListener(v ->
                Toast.makeText(LocationActivity.this, "Live location sharing started!", Toast.LENGTH_SHORT).show()
        );

        btnStop.setOnClickListener(v ->
                Toast.makeText(LocationActivity.this, "Location sharing stopped.", Toast.LENGTH_SHORT).show()
        );

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(LocationActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(LocationActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(LocationActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(LocationActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(LocationActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
'''

files[os.path.join(activities_dir, "ContactsActivity.java")] = r'''package com.example.yuwathi.activities;

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
        setContentView(R.layout.activity_contacts);

        RecyclerView rvContacts = findViewById(R.id.rv_contacts);
        MaterialButton btnAddContact = findViewById(R.id.btn_add_contact);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setSelectedItemId(R.id.nav_contacts);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        btnAddContact.setOnClickListener(v ->
                Toast.makeText(ContactsActivity.this, "Add contact dialog - coming soon!", Toast.LENGTH_SHORT).show()
        );

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ContactsActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                return true;
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
'''

files[os.path.join(activities_dir, "SafetyTipsActivity.java")] = r'''package com.example.yuwathi.activities;

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
'''

files[os.path.join(activities_dir, "ComplaintActivity.java")] = r'''package com.example.yuwathi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class ComplaintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        String[] categories = {
                "Harassment", "Stalking", "Physical Assault",
                "Suspicious Following", "Cyber-bullying", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        MaterialButton btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v ->
                startActivity(new Intent(ComplaintActivity.this, Complaint2Activity.class))
        );
    }
}
'''

files[os.path.join(activities_dir, "Complaint2Activity.java")] = r'''package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class Complaint2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint2);

        Spinner spinnerContact = findViewById(R.id.spinner_contact_pref);
        String[] contactOptions = {"In-app notification only", "Phone call", "SMS", "Do not contact"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactOptions);
        spinnerContact.setAdapter(adapter);

        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(Complaint2Activity.this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Complaint2Activity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
'''

files[os.path.join(activities_dir, "ProfileActivity.java")] = r'''package com.example.yuwathi.activities;

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
'''

files[os.path.join(activities_dir, "SplashActivity.java")] = r'''package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.iv_logo);
        TextView title = findViewById(R.id.tv_app_name);

        if (logo != null) {
            logo.setAlpha(0f);
            logo.animate().alpha(1f).setDuration(800).start();
        }
        if (title != null) {
            title.setAlpha(0f);
            title.animate().alpha(1f).setDuration(800).setStartDelay(300).start();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 2500);
    }
}
'''

files[os.path.join(activities_dir, "SosActivity.java")] = r'''package com.example.yuwathi.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class SosActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean isActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        MaterialButton btnSos = findViewById(R.id.btn_sos_main);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel_sos);
        TextView tvStatus = findViewById(R.id.tv_sos_status);
        TextView tvCountdown = findViewById(R.id.tv_countdown);

        btnSos.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isActivated) startCountdown(tvCountdown, tvStatus);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!isActivated) cancelCountdown(tvCountdown, tvStatus);
                    return true;
                default:
                    return false;
            }
        });

        btnCancel.setOnClickListener(v -> {
            cancelCountdown(tvCountdown, tvStatus);
            finish();
        });
    }

    private void startCountdown(TextView tvCountdown, TextView tvStatus) {
        tvStatus.setText(getString(R.string.sos_hold_message));
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                isActivated = true;
                tvCountdown.setText("");
                tvStatus.setText(getString(R.string.sos_activated));
                activateSOS();
            }
        }.start();
    }

    private void cancelCountdown(TextView tvCountdown, TextView tvStatus) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        tvCountdown.setText("");
        tvStatus.setText("Hold the button to send alerts");
    }

    private void activateSOS() {
        Toast.makeText(this, "SOS Alert Sent to Emergency Contacts!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
'''

files[os.path.join(utils_dir, "SOSHelper.java")] = r'''package com.example.yuwathi.utils;

import android.content.Context;

public class SOSHelper {

    private final Context context;

    public SOSHelper(Context context) {
        this.context = context;
    }

    // Helper code removed
}
'''

for filepath, content in files.items():
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content.lstrip('\n'))
    print(f"Written: {os.path.basename(filepath)}")

print("\nDone! All Java files written.")

