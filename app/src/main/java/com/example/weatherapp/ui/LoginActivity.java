package com.example.weatherapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.example.weatherapp.data.DBHelper;
import com.example.weatherapp.util.MD5Util;


public class LoginActivity extends BaseActivity {

    private EditText edtUsername, edtPassword;
    private CheckBox chkRemember;
    private Button btnLogin, btnBack;
    private ImageView imgTogglePassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);

        // Load dữ liệu từ SharedPreferences nếu có
        SharedPreferences pref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isRemembered = pref.getBoolean("remember", false);
        if (isRemembered) {
            edtUsername.setText(pref.getString("username", ""));
            edtPassword.setText(pref.getString("password", ""));
            chkRemember.setChecked(true);
        }

        // Reset trạng thái hiển thị mật khẩu về mặc định (ẩn)
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        imgTogglePassword.setImageResource(R.drawable.ic_lock);
        isPasswordVisible = false;

        // Sự kiện nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.login_error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String hashedPassword = MD5Util.hash(password);

            Cursor cursor = db.query(DBHelper.TABLE_USERS,
                    null,
                    DBHelper.COLUMN_USERNAME + "=? AND " + DBHelper.COLUMN_PASSWORD + "=?",
                    new String[]{username, hashedPassword},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Thành công: lưu nếu cần
                if (chkRemember.isChecked()) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("remember", true);
                    editor.apply();
                } else {
                    pref.edit().clear().apply();
                }

                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, getString(R.string.login_error_invalid), Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) cursor.close();
            db.close();
        });

        // Toggle mật khẩu hiển thị
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

        // Nút quay lại
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
