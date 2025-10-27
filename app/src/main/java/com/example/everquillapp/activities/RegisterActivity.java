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

public class RegisterActivity extends AppCompatActivity {
    
    private EditText editName, editEmail, editPassword, editConfirmPassword;
    private Button btnRegister;
    private TextView txtLogin;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Initialize views
        initViews();
        
        // Initialize API and TokenManager
        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);
        
        // Set click listeners
        btnRegister.setOnClickListener(v -> performRegister());
        txtLogin.setOnClickListener(v -> finish());
    }
    
    private void initViews() {
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        txtLogin = findViewById(R.id.txt_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void performRegister() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        
        // Validation
        if (name.isEmpty()) {
            editName.setError("Name is required");
            editName.requestFocus();
            return;
        }
        
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
        
        if (password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            editPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        }
        
        // Show loading
        setLoading(true);
        
        // Prepare request body
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);
        
        // API call
        apiService.register(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                setLoading(false);
                
                android.util.Log.d("RegisterActivity", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    android.util.Log.d("RegisterActivity", "Response: " + new Gson().toJson(apiResponse));
                    
                    // Check if registration succeeded
                    String message = apiResponse.getMessage();
                    
                    // Backend returns message about verification email
                    if (message != null && message.contains("verification")) {
                        Toast.makeText(RegisterActivity.this, 
                                "Registration successful! Please verify your email.", 
                                Toast.LENGTH_LONG).show();
                        
                        // Navigate to verification screen
                        Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                        intent.putExtra("email", email);
                        
                        // Get dev code if available
                        try {
                            Map<String, Object> dataMap = (Map<String, Object>) apiResponse.getData();
                            if (dataMap != null && dataMap.containsKey("devVerificationCode")) {
                                intent.putExtra("devCode", dataMap.get("devVerificationCode").toString());
                            }
                        } catch (Exception e) {
                            android.util.Log.e("RegisterActivity", "Could not extract dev code", e);
                        }
                        
                        startActivity(intent);
                        finish();
                    } else if (apiResponse.getAccessToken() != null) {
                        // Old flow: direct login after register
                        tokenManager.saveToken(apiResponse.getAccessToken());
                        
                        User user = apiResponse.getUser();
                        if (user != null) {
                            Gson gson = new Gson();
                            String userJson = gson.toJson(user);
                            tokenManager.saveUserData(userJson);
                        }
                        
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(RegisterActivity.this, 
                                message != null ? message : "Registration completed", 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
        editName.setEnabled(!isLoading);
        editEmail.setEnabled(!isLoading);
        editPassword.setEnabled(!isLoading);
        editConfirmPassword.setEnabled(!isLoading);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

