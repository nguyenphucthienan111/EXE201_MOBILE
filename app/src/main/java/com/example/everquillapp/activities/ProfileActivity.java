package com.example.everquillapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.everquillapp.R;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.User;
import com.example.everquillapp.utils.TokenManager;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    
    private ImageView imgAvatar;
    private TextView txtName, txtEmail, txtPlan;
    private Button btnChangeAvatar, btnEditProfile, btnChangePassword, btnUpgradePremium;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private TokenManager tokenManager;
    private User currentUser;
    
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Initialize API and TokenManager
        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);
        
        // Initialize views
        initViews();
        
        // Setup image picker launcher
        setupImagePicker();
        
        // Load user data
        loadUserData();
        
        // Set click listeners
        btnChangeAvatar.setOnClickListener(v -> pickImage());
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> navigateToChangePassword());
        btnUpgradePremium.setOnClickListener(v -> navigateToPremium());
    }
    
    private void initViews() {
        imgAvatar = findViewById(R.id.img_avatar);
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
        txtPlan = findViewById(R.id.txt_plan);
        btnChangeAvatar = findViewById(R.id.btn_change_avatar);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnUpgradePremium = findViewById(R.id.btn_upgrade_premium);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            uploadAvatar(imageUri);
                        }
                    }
                });
    }
    
    private void loadUserData() {
        showLoading(true);
        
        apiService.getCurrentUser().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        currentUser = apiResponse.getData();
                        displayUserInfo();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayUserInfo() {
        txtName.setText(currentUser.getName());
        txtEmail.setText(currentUser.getEmail());
        
        String planText = currentUser.isPremium() ? "Premium User" : "Free Plan";
        txtPlan.setText(planText);
        
        // Show/hide upgrade button
        btnUpgradePremium.setVisibility(currentUser.isPremium() ? View.GONE : View.VISIBLE);
        
        // Load avatar
        if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(currentUser.getAvatar())
                    .placeholder(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(imgAvatar);
        }
    }
    
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    
    private void uploadAvatar(Uri imageUri) {
        // TODO: Convert Uri to File and upload
        Toast.makeText(this, "Avatar upload - Implementation needed", Toast.LENGTH_SHORT).show();
    }
    
    private void showEditProfileDialog() {
        // TODO: Show dialog to edit name
        Toast.makeText(this, "Edit Profile - Coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void navigateToChangePassword() {
        // TODO: Navigate to ChangePasswordActivity
        Toast.makeText(this, "Change Password - Coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void navigateToPremium() {
        Intent intent = new Intent(ProfileActivity.this, PremiumActivity.class);
        startActivity(intent);
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

