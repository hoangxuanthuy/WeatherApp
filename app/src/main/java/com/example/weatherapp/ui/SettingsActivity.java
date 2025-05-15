package com.example.weatherapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private TextView tvGreeting, tvUsername, tvEmail;
    private ImageView imgAvatar;
    private Button btnResetPassword, btnLogout, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ view
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnSettings = findViewById(R.id.btnSettings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            SharedPreferences pref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String savedUsername = pref.getString("username", null);
            String displayName = user.getDisplayName();

            // Ưu tiên tên từ Firebase, nếu không thì dùng tên đã lưu, nếu vẫn không có thì fallback
            String finalName = (displayName != null && !displayName.isEmpty())
                    ? displayName
                    : (savedUsername != null ? savedUsername : getString(R.string.no_username));

            // Set hiển thị
            tvGreeting.setText(getString(R.string.hello_user_name, finalName));
            tvUsername.setText(getString(R.string.username_label, finalName));
            tvEmail.setText(getString(R.string.email_label, user.getEmail()));

            imgAvatar.setImageResource(R.drawable.ic_user_avatar);
        }

        // Đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> {
            if (user != null && user.getEmail() != null) {
                mAuth.sendPasswordResetEmail(user.getEmail());
                showToast(getString(R.string.reset_password_sent));
            }
        });

        // Cài đặt thêm
        btnSettings.setOnClickListener(v -> {
            showToast(getString(R.string.setting_feature_coming));
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            showToast(getString(R.string.logout_success));
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Thanh điều hướng
        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_analysis) {
                startActivity(new Intent(this, AnalysisActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }

            return false;
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
