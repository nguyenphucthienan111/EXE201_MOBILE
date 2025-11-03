package com.example.everquillapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.everquillapp.R;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    
    private TextView txtTotalJournals, txtThisWeek, txtThisMonth;
    private PieChart pieChartMoods;
    private BarChart barChartActivity;
    private ProgressBar progressBar;
    
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
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
        initViews();
        
        // Load dashboard data
        loadDashboard();
    }
    
    private void initViews() {
        txtTotalJournals = findViewById(R.id.txt_total_journals);
        txtThisWeek = findViewById(R.id.txt_this_week);
        txtThisMonth = findViewById(R.id.txt_this_month);
        pieChartMoods = findViewById(R.id.pie_chart_moods);
        barChartActivity = findViewById(R.id.bar_chart_activity);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void loadDashboard() {
        showLoading(true);
        
        apiService.getDashboard("month").enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Map<String, Object>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        displayDashboard(apiResponse.getData());
                    } else {
                        Toast.makeText(DashboardActivity.this, "Failed to load dashboard", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load dashboard", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void displayDashboard(Map<String, Object> data) {
        // Stats
        Object statsObj = data.get("journalStats");
        if (statsObj instanceof Map) {
            Object total = ((Map<String, Object>) statsObj).get("totalEntries");
            if (total != null) txtTotalJournals.setText(String.valueOf(total));
        }

        // Mood trends chartData -> build distributions & series
        Map<String, Object> moodTrends = null;
        Object mt = data.get("moodTrends");
        if (mt instanceof Map) moodTrends = (Map<String, Object>) mt;
        List<Map<String, Object>> chartData = new ArrayList<>();
        if (moodTrends != null) {
            Object cd = moodTrends.get("chartData");
            if (cd instanceof List) chartData = (List<Map<String, Object>>) cd;
        }

        setupMoodPieChartFrom(chartData);
        setupActivityBarChartFrom(chartData);
    }

    private void setupMoodPieChartFrom(List<Map<String, Object>> chartData) {
        // Categorize scores into 4 buckets using thresholds
        float happy=0, calm=0, anxious=0, sad=0;
        for (Map<String, Object> item : chartData) {
            double score = toDouble(item.get("score"));
            if (score >= 7) happy += 1f;
            else if (score >= 5) calm += 1f;
            else if (score >= 3) anxious += 1f;
            else sad += 1f;
        }

        if (happy+calm+anxious+sad == 0) {
            happy = 1; calm = anxious = sad = 1; // fallback equal parts
        }

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(happy, "Happy"));
        entries.add(new PieEntry(calm, "Calm"));
        entries.add(new PieEntry(anxious, "Anxious"));
        entries.add(new PieEntry(sad, "Sad"));

        PieDataSet dataSet = new PieDataSet(entries, "Mood Distribution");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        
        PieData data = new PieData(dataSet);
        pieChartMoods.setData(data);
        pieChartMoods.getDescription().setEnabled(false);
        pieChartMoods.setDrawHoleEnabled(true);
        pieChartMoods.setHoleColor(Color.WHITE);
        pieChartMoods.setTransparentCircleRadius(58f);
        pieChartMoods.animateY(1000);
        pieChartMoods.invalidate();
    }
    
    private void setupActivityBarChartFrom(List<Map<String, Object>> chartData) {
        // Use mood score series as activity (proxy). Shows last N points.
        List<BarEntry> entries = new ArrayList<>();
        int i = 1;
        for (Map<String, Object> item : chartData) {
            float y = (float) toDouble(item.get("score"));
            entries.add(new BarEntry(i++, y));
        }
        if (entries.isEmpty()) {
            entries.add(new BarEntry(1f, 0f));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Mood Score Trend");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        
        BarData data = new BarData(dataSet);
        barChartActivity.setData(data);
        barChartActivity.getDescription().setEnabled(false);
        barChartActivity.animateY(1000);
        barChartActivity.invalidate();
    }

    private double toDouble(Object v) {
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(String.valueOf(v)); } catch (Exception e) { return 0d; }
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


