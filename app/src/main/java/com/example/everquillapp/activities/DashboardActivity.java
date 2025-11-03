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
    
    private void displayDashboard(Map<String, Object> data) {
        // Display stats
        Object total = data.get("totalJournals");
        if (total != null) {
            txtTotalJournals.setText(String.valueOf(total));
        }
        
        // Setup Mood Pie Chart
        setupMoodPieChart();
        
        // Setup Activity Bar Chart  
        setupActivityBarChart();
    }
    
    private void setupMoodPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Happy"));
        entries.add(new PieEntry(20f, "Sad"));
        entries.add(new PieEntry(15f, "Calm"));
        entries.add(new PieEntry(20f, "Anxious"));
        entries.add(new PieEntry(15f, "Excited"));
        
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
    
    private void setupActivityBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 5));
        entries.add(new BarEntry(2f, 8));
        entries.add(new BarEntry(3f, 3));
        entries.add(new BarEntry(4f, 10));
        entries.add(new BarEntry(5f, 7));
        entries.add(new BarEntry(6f, 6));
        entries.add(new BarEntry(7f, 12));
        
        BarDataSet dataSet = new BarDataSet(entries, "Journals per Day");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        
        BarData data = new BarData(dataSet);
        barChartActivity.setData(data);
        barChartActivity.getDescription().setEnabled(false);
        barChartActivity.animateY(1000);
        barChartActivity.invalidate();
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


