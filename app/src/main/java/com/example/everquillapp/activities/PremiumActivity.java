package com.example.everquillapp.activities;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
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
    
    private Button btnMonthly;
    private ProgressBar progressBar;
    private ApiService apiService;
    private Button btnCurrentPlanFree;

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
        btnCurrentPlanFree = findViewById(R.id.btn_current_plan);
        progressBar = findViewById(R.id.progress_bar);
        
        // Set click listeners
        btnMonthly.setOnClickListener(v -> subscribePlan("monthly", 41000));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPlanUi();
    }
    
    private void refreshPlanUi() {
        apiService.checkUserPlan().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Map<String, Object> data = response.body().getData();
                    String currentPlan = data != null && data.get("currentPlan") != null ? data.get("currentPlan").toString() : "free";
                    if ("premium".equalsIgnoreCase(currentPlan)) {
                        // Pro is current plan
                        btnMonthly.setText(R.string.premium_your_current_plan);
                        btnMonthly.setEnabled(false);
                        if (btnCurrentPlanFree != null) {
                            btnCurrentPlanFree.setText("Free plan");
                            btnCurrentPlanFree.setEnabled(false);
                        }
                    } else {
                        btnMonthly.setText(R.string.premium_upgrade_to_pro);
                        btnMonthly.setEnabled(true);
                        if (btnCurrentPlanFree != null) {
                            btnCurrentPlanFree.setText(R.string.premium_your_current_plan);
                            btnCurrentPlanFree.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) { }
        });
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
                            String paymentId = data.get("paymentId");
                            if (paymentId != null) {
                                getSharedPreferences("everquill", MODE_PRIVATE)
                                        .edit()
                                        .putString("lastPaymentId", paymentId)
                                        .apply();
                            }
                            // BE returns 'paymentUrl'; fallback to 'checkoutUrl' just in case
                            String checkoutUrl = data.get("paymentUrl");
                            if (checkoutUrl == null) checkoutUrl = data.get("checkoutUrl");
                        
                        if (checkoutUrl != null) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(PremiumActivity.this, "Open payment URL failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PremiumActivity.this, "Payment link not available", Toast.LENGTH_SHORT).show();
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


