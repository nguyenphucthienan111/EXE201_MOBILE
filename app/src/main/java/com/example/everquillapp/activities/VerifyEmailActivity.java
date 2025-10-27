package com.example.everquillapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.everquillapp.R;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.User;
import com.example.everquillapp.utils.TokenManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {
    
    private EditText editVerificationCode;
    private Button btnVerify, btnResendCode;
    private TextView txtEmail, txtDevCode;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private TokenManager tokenManager;
    private String userEmail;
    private String devCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        
        // Initialize API and TokenManager
        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);
        
        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        devCode = getIntent().getStringExtra("devCode");
        
        // Initialize views
        initViews();
        
        // Display email
        if (userEmail != null) {
            txtEmail.setText("Verification code sent to: " + userEmail);
        }
        
        // Display dev code if in development
        if (devCode != null && !devCode.isEmpty()) {
            txtDevCode.setText("Development Code: " + devCode);
            txtDevCode.setVisibility(View.VISIBLE);
        }
        
        // Set click listeners
        btnVerify.setOnClickListener(v -> performVerification());
        btnResendCode.setOnClickListener(v -> resendCode());
    }
    
    private void initViews() {
        editVerificationCode = findViewById(R.id.edit_verification_code);
        btnVerify = findViewById(R.id.btn_verify);
        btnResendCode = findViewById(R.id.btn_resend_code);
        txtEmail = findViewById(R.id.txt_email);
        txtDevCode = findViewById(R.id.txt_dev_code);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void performVerification() {
        String code = editVerificationCode.getText().toString().trim();
        
        if (code.isEmpty()) {
            editVerificationCode.setError("Verification code is required");
            editVerificationCode.requestFocus();
            return;
        }
        
        if (code.length() != 6) {
            editVerificationCode.setError("Code must be 6 characters");
            editVerificationCode.requestFocus();
            return;
        }
        
        setLoading(true);
        
        Map<String, String> body = new HashMap<>();
        body.put("email", userEmail);
        body.put("code", code.toUpperCase());
        
        // Call verify API
        apiService.verifyEmailWithCode(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                setLoading(false);
                
                android.util.Log.d("VerifyEmail", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    android.util.Log.d("VerifyEmail", "Response: " + new Gson().toJson(apiResponse));
                    
                    // Check for token
                    String token = apiResponse.getAccessToken();
                    if (token == null) {
                        token = apiResponse.getData() != null ? 
                                new Gson().toJson(apiResponse.getData()) : null;
                    }
                    
                    if (token != null && !token.isEmpty()) {
                        // Save token
                        tokenManager.saveToken(token);
                        
                        // Save user
                        User user = apiResponse.getUser();
                        if (user != null) {
                            String userJson = new Gson().toJson(user);
                            tokenManager.saveUserData(userJson);
                        }
                        
                        Toast.makeText(VerifyEmailActivity.this, 
                                "Email verified successfully!", 
                                Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(VerifyEmailActivity.this, 
                                apiResponse.getMessage() != null ? 
                                        apiResponse.getMessage() : "Verification successful! Please login.", 
                                Toast.LENGTH_LONG).show();
                        navigateToLogin();
                    }
                } else {
                    Toast.makeText(VerifyEmailActivity.this, 
                            "Invalid verification code", 
                            Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                setLoading(false);
                android.util.Log.e("VerifyEmail", "Error: " + t.getMessage(), t);
                Toast.makeText(VerifyEmailActivity.this, 
                        "Network error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void resendCode() {
        Toast.makeText(this, "Resend code - Coming soon", Toast.LENGTH_SHORT).show();
        // TODO: Implement resend verification code
    }
    
    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnVerify.setEnabled(!isLoading);
        editVerificationCode.setEnabled(!isLoading);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(VerifyEmailActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

