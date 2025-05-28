package com.example.weatherapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.weatherapp.R;
import com.google.firebase.auth.*;

public class ChangePasswordActivity extends BaseActivity {

    private EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private ImageView imgToggleNewPassword, imgToggleConfirmPassword;
    private Button btnChangePassword, btnBack;
    private boolean isNewPasswordVisible = false, isConfirmVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        imgToggleNewPassword = findViewById(R.id.imgToggleNewPassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        imgToggleNewPassword.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            edtNewPassword.setInputType(isNewPasswordVisible ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgToggleNewPassword.setImageResource(isNewPasswordVisible ? R.drawable.ic_unlock : R.drawable.ic_lock);
            edtNewPassword.setSelection(edtNewPassword.getText().length());
        });

        imgToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            edtConfirmPassword.setInputType(isConfirmVisible ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgToggleConfirmPassword.setImageResource(isConfirmVisible ? R.drawable.ic_unlock : R.drawable.ic_lock);
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
        });

        btnChangePassword.setOnClickListener(v -> {
            if (user == null || user.getEmail() == null) {
                Toast.makeText(this, "Không thể xác thực người dùng!", Toast.LENGTH_SHORT).show();
                return;
            }

            for (UserInfo profile : user.getProviderData()) {
                if (profile.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                    Toast.makeText(this, "Tài khoản Google không thể đổi mật khẩu trong ứng dụng", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            String current = edtCurrentPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(current) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), current);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
