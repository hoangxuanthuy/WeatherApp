package com.example.weatherapp.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.example.weatherapp.data.DBHelper;
import com.example.weatherapp.util.LocaleHelper;
import com.example.weatherapp.util.MD5Util;

public class RegisterActivity extends BaseActivity {

    EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    ImageView imgTogglePassword, imgToggleConfirmPassword;
    Button btnRegister, btnBack;

    boolean isPasswordVisible = false;
    boolean isConfirmVisible = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getSavedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        // Đặt biểu tượng ban đầu là khóa (ẩn mật khẩu)
        imgTogglePassword.setImageResource(R.drawable.ic_lock);
        imgToggleConfirmPassword.setImageResource(R.drawable.ic_lock);

        imgTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_unlock);
            } else {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_lock);
            }
            edtPassword.setSelection(edtPassword.getText().length());
        });

        imgToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            if (isConfirmVisible) {
                edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgToggleConfirmPassword.setImageResource(R.drawable.ic_unlock);
            } else {
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgToggleConfirmPassword.setImageResource(R.drawable.ic_lock);
            }
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_fill_all), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 3) {
                Toast.makeText(this, getString(R.string.error_password_length), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, getString(R.string.error_password_confirm), Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_USERNAME, username);
            values.put(DBHelper.COLUMN_EMAIL, email);
            values.put(DBHelper.COLUMN_PASSWORD, MD5Util.hash(password));

            long result = db.insert(DBHelper.TABLE_USERS, null, values);
            if (result != -1) {
                Toast.makeText(this, getString(R.string.success_register), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
