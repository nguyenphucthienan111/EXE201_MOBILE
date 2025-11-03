package com.example.everquillapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.everquillapp.R;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumActivity extends AppCompatActivity {
    
    private Button btnMonthly, btnYearly;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        // Ensure toolbar back works
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Initialize API
        apiService = ApiClient.getApiService(this);
        
        // Initialize views
        btnMonthly = findViewById(R.id.btn_monthly);
        btnYearly = findViewById(R.id.btn_yearly);
        progressBar = findViewById(R.id.progress_bar);
        
        // Set click listeners
        btnMonthly.setOnClickListener(v -> subscribePlan("monthly", 99000));
        btnYearly.setOnClickListener(v -> subscribePlan("yearly", 999000));
    }
    
    private void subscribePlan(String plan, int amount) {
        showLoading(true);
        
        Map<String, Object> body = new HashMap<>();
        body.put("plan", plan);
        body.put("amount", amount);
        body.put("returnUrl", "everquill://payment/success");
        body.put("cancelUrl", "everquill://payment/cancel");
        
        apiService.createPayment(body).enqueue(new Callback<ApiResponse<Map<String, String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, String>>> call, Response<ApiResponse<Map<String, String>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Map<String, String>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Map<String, String> data = apiResponse.getData();
                        String checkoutUrl = data.get("checkoutUrl");
                        
                        if (checkoutUrl != null) {
                            // TODO: Open WebView or external browser for payment
                            Toast.makeText(PremiumActivity.this, "Payment URL: " + checkoutUrl, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(PremiumActivity.this, "Failed to create payment", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PremiumActivity.this, "Payment failed", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Map<String, String>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(PremiumActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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


