package com.example.everquillapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity {
    
    private EditText editEmail, editPassword;
    private Button btnLogin;
    private TextView txtRegister, txtForgotPassword;
    private CheckBox checkRememberMe;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize views
        initViews();
        
        // Initialize API and TokenManager
        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);
        
        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToMain();
            return;
        }
        
        // Set click listeners
        btnLogin.setOnClickListener(v -> performLogin());
        txtRegister.setOnClickListener(v -> navigateToRegister());
        txtForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
    }
    
    private void initViews() {
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        checkRememberMe = findViewById(R.id.check_remember_me);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void performLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        boolean rememberMe = checkRememberMe.isChecked();
        
        // Validation
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            editPassword.setError("Password is required");
            editPassword.requestFocus();
            return;
        }
        
        // Show loading
        setLoading(true);
        
        // Prepare request body
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("rememberMe", String.valueOf(rememberMe));
        
        // API call
        apiService.login(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                setLoading(false);
                
                android.util.Log.d("LoginActivity", "Response code: " + response.code());
                
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        android.util.Log.e("LoginActivity", "Response body is null");
                        Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    ApiResponse<User> apiResponse = response.body();
                    
                    android.util.Log.d("LoginActivity", "Response body: " + new Gson().toJson(apiResponse));
                    
                    // Check if we have access_token (success indicator)
                    String token = apiResponse.getAccessToken();
                    User user = apiResponse.getUser();
                    
                    android.util.Log.d("LoginActivity", "Token: " + (token != null ? "present" : "null"));
                    android.util.Log.d("LoginActivity", "User: " + (user != null ? user.getEmail() : "null"));
                    
                    if (token != null && !token.isEmpty()) {
                        // Save token and user data
                        tokenManager.saveToken(token);
                        
                        if (user != null) {
                            Gson gson = new Gson();
                            String userJson = gson.toJson(user);
                            tokenManager.saveUserData(userJson);
                            
                            android.util.Log.d("LoginActivity", "User saved: " + userJson);
                        }
                        
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Login failed - no token received";
                        android.util.Log.e("LoginActivity", "Login failed: " + errorMsg);
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    android.util.Log.e("LoginActivity", "Response not successful or null");
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                setLoading(false);
                android.util.Log.e("LoginActivity", "Network error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        editEmail.setEnabled(!isLoading);
        editPassword.setEnabled(!isLoading);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    
    private void navigateToForgotPassword() {
        // TODO: Implement ForgotPasswordActivity
        Toast.makeText(this, "Forgot Password - Coming soon", Toast.LENGTH_SHORT).show();
    }
}

