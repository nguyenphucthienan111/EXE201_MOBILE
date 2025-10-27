package com.example.everquillapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everquillapp.R;
import com.example.everquillapp.adapters.ReviewAdapter;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {
    
    private RecyclerView recyclerReviews;
    private ReviewAdapter adapter;
    private ProgressBar progressBar;
    private Button btnAddReview;
    
    private ApiService apiService;
    private List<Map<String, Object>> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reviews");
        }
        
        // Initialize API
        apiService = ApiClient.getApiService(this);
        
        // Initialize views
        recyclerReviews = findViewById(R.id.recycler_reviews);
        progressBar = findViewById(R.id.progress_bar);
        btnAddReview = findViewById(R.id.btn_add_review);
        
        // Setup RecyclerView
        adapter = new ReviewAdapter(this, reviewList);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(adapter);
        
        // Load reviews
        loadReviews();
        
        // Add review button
        btnAddReview.setOnClickListener(v -> showAddReviewDialog());
    }
    
    private void loadReviews() {
        showLoading(true);
        
        apiService.getReviews(1).enqueue(new Callback<ApiResponse<List<Map<String, Object>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Map<String, Object>>>> call, Response<ApiResponse<List<Map<String, Object>>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Map<String, Object>>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        reviewList.clear();
                        reviewList.addAll(apiResponse.getData());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Map<String, Object>>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ReviewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showAddReviewDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        EditText editComment = dialogView.findViewById(R.id.edit_comment);
        
        new AlertDialog.Builder(this)
                .setTitle("Add Review")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    String comment = editComment.getText().toString().trim();
                    submitReview(rating, comment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void submitReview(float rating, String comment) {
        Map<String, Object> body = new HashMap<>();
        body.put("rating", (int) rating);
        body.put("comment", comment);
        
        apiService.createReview(body).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ReviewsActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                    loadReviews();
                } else {
                    Toast.makeText(ReviewsActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ReviewsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

