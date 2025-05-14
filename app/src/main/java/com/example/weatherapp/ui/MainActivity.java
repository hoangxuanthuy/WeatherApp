package com.example.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.weatherapp.R;
import com.example.weatherapp.util.LocaleHelper;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    Button btnLogin, btnRegister, btnGoogle;
    TextView tvLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Google Sign-In Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // phải giống với client ID web từ Firebase
                .requestEmail()
                .build();


        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // View mapping
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvLanguage = findViewById(R.id.tvLanguage);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        btnGoogle.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            } else {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });

        tvLanguage.setOnClickListener(v -> {
            String currentLang = LocaleHelper.getSavedLanguage(this);
            String newLang = currentLang.equals("vi") ? "en" : "vi";
            LocaleHelper.setLocale(this, newLang);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, handle the error
                int statusCode = e.getStatusCode();

                // Kiểm tra cả lỗi code 10 và 12501 như là trường hợp người dùng hủy bỏ
                if (statusCode == com.google.android.gms.common.api.CommonStatusCodes.CANCELED || statusCode == 12501) {
                    // User canceled the sign-in flow or an unknown error related to cancellation occurred
                    Log.d("GOOGLE_SIGN_IN", "Google sign in canceled or unknown error: " + statusCode);
                    Toast.makeText(this, "Đăng nhập Google đã bị hủy hoặc gặp sự cố", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle other potential error codes
                    Log.e("GOOGLE_SIGN_IN", "Đăng nhập Google thất bại: " + statusCode, e);
                    Toast.makeText(this, "Đăng nhập Google thất bại: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        Log.e("FIREBASE_AUTH", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Xác thực với Firebase thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
