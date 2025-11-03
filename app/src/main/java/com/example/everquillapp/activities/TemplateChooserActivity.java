package com.example.everquillapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everquillapp.R;
import com.example.everquillapp.adapters.TemplateAdapter;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.Template;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TemplateChooserActivity extends AppCompatActivity implements TemplateAdapter.OnTemplateClickListener {
    
    private RecyclerView recyclerTemplates;
    private TemplateAdapter adapter;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private List<Template> templateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_chooser);
        
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Choose Template");
        }
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        // Ensure back navigation works from toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Initialize API
        apiService = ApiClient.getApiService(this);
        
        // Initialize views
        recyclerTemplates = findViewById(R.id.recycler_templates);
        progressBar = findViewById(R.id.progress_bar);
        
        // Setup RecyclerView
        adapter = new TemplateAdapter(this, templateList, this);
        recyclerTemplates.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerTemplates.setAdapter(adapter);
        
        // Load templates
        loadTemplates();
    }
    
    private void loadTemplates() {
        showLoading(true);
        
        apiService.getTemplates().enqueue(new Callback<ApiResponse<List<Template>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Template>>> call, Response<ApiResponse<List<Template>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Template>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        templateList.clear();
                        templateList.addAll(apiResponse.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TemplateChooserActivity.this, "Failed to load templates", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TemplateChooserActivity.this, "Failed to load templates", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Template>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(TemplateChooserActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onTemplateClick(Template template) {
        // Return selected template
        Intent resultIntent = new Intent();
        resultIntent.putExtra("template_id", template.getId());
        resultIntent.putExtra("template_name", template.getName());
        setResult(RESULT_OK, resultIntent);
        finish();
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


