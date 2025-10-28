package com.example.everquillapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.everquillapp.R;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.User;
import com.example.everquillapp.utils.TokenManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Init
        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);

        initViews();
        setupImagePicker();

        // Load data
        loadUserData();

        // Click listeners
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

        apiService.getCurrentUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    displayUserInfo();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserInfo() {
        String name = currentUser.getName() != null && !currentUser.getName().isEmpty()
                ? currentUser.getName()
                : "User";

        txtName.setText(name);
        txtEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "No email");

        String planText = currentUser.isPremium() ? "Premium User" : "Free Plan";
        txtPlan.setText(planText);

        btnUpgradePremium.setVisibility(currentUser.isPremium() ? View.GONE : View.VISIBLE);

        if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            // Thêm query param timestamp để Glide reload ảnh mới
            String avatarUrl = currentUser.getAvatar() + "?t=" + System.currentTimeMillis();

            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .circleCrop()
                    .skipMemoryCache(true) // bỏ cache RAM
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.mipmap.ic_launcher);
        }
    }


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Upload avatar với Retrofit
    private void uploadAvatar(Uri imageUri) {
        showLoading(true);

        try {
            File file = createFileFromUri(imageUri);
            if (file == null) {
                showLoading(false);
                Toast.makeText(this, "Failed to get image file", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            apiService.uploadAvatar(body).enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        currentUser = response.body().getData();
                        displayUserInfo();
                        Toast.makeText(ProfileActivity.this, "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    showLoading(false);
                    Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            showLoading(false);
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Tạo file tạm từ Uri
    private File createFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) return null;

        File tempFile = new File(getCacheDir(), "avatar_temp");
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    // Edit profile dialog
    private void showEditProfileDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "User not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        EditText edtName = dialogView.findViewById(R.id.edt_name);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        edtName.setText(currentUser.getName());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (newName.isEmpty()) {
                edtName.setError("Name cannot be empty");
                return;
            }
            dialog.dismiss();
            updateProfile(newName);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateProfile(String newName) {
        showLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("name", newName);

        apiService.updateProfile(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentUser = response.body().getData();
                    displayUserInfo();
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToChangePassword() {
        Toast.makeText(this, "Change Password - coming soon", Toast.LENGTH_SHORT).show();
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
