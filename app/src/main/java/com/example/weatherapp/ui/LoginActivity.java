package com.example.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.example.weatherapp.util.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends BaseActivity {

    private EditText edtEmail, edtPassword;
    private CheckBox chkRemember;
    private Button btnLogin, btnBack;
    private ImageView imgTogglePassword;
    private boolean isPasswordVisible = false;

    private FirebaseAuth mAuth;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences pref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isRemembered = pref.getBoolean("remember", false);
        if (isRemembered) {
            edtEmail.setText(pref.getString("email", ""));
            edtPassword.setText(pref.getString("password", ""));
            chkRemember.setChecked(true);
        }

        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        imgTogglePassword.setImageResource(R.drawable.ic_lock);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showToast(R.string.login_error_empty_fields);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (chkRemember.isChecked()) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.putBoolean("remember", true);
                                    editor.putBoolean("isLoggedIn", true);

                                    // Nếu username được set trước đó trong Register thì lưu lại
                                    if (user.getDisplayName() != null) {
                                        editor.putString("username", user.getDisplayName());
                                    }

                                    editor.apply();
                                } else {
                                    pref.edit().clear().apply();
                                }

                                showToast(R.string.login_success);
                                startActivity(new Intent(this, SettingsActivity.class));
                                finish();
                            }
                        } else {
                            showToast(R.string.login_error_invalid);
                        }
                    });
        });

        imgTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_lock);
            } else {
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_unlock);
            }
            edtPassword.setSelection(edtPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void showToast(int msgResId) {
        Toast.makeText(this, getString(msgResId), Toast.LENGTH_SHORT).show();
    }
}
