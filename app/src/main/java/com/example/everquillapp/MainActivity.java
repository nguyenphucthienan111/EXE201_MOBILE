package com.example.everquillapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.everquillapp.R;
import com.example.everquillapp.adapters.JournalAdapter;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.Journal;
import com.example.everquillapp.utils.TokenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements JournalAdapter.OnJournalClickListener {
    
    private RecyclerView recyclerView;
    private JournalAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private View emptyView;
    private FloatingActionButton fabCreate;
    private BottomNavigationView bottomNav;
    
    private ApiService apiService;
    private TokenManager tokenManager;
    private List<Journal> journalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize API and TokenManager
        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);
        
        // Check if logged in
        if (!tokenManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        // Initialize views
        initViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Load journals
        loadJournals();
        
        // Setup FAB
        fabCreate.setOnClickListener(v -> createNewJournal());
        
        // Setup SwipeRefresh
        swipeRefresh.setOnRefreshListener(this::loadJournals);
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_journals);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        fabCreate = findViewById(R.id.fab_create);
        bottomNav = findViewById(R.id.bottom_navigation);
        
        // Setup bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_journals);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_journals) {
                // Already on journals screen
                return true;
            } else if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_notifications) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            
            return false;
        });
    }
    
    private void setupRecyclerView() {
        adapter = new JournalAdapter(this, journalList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadJournals() {
        showLoading(true);
        
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "100");
        
        apiService.getJournals(params).enqueue(new Callback<List<Journal>>() {
            @Override
            public void onResponse(Call<List<Journal>> call, Response<List<Journal>> response) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    journalList.clear();
                    journalList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateEmptyView();
                } else {
                    Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<Journal>> call, Throwable t) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void updateEmptyView() {
        if (journalList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void createNewJournal() {
        Intent intent = new Intent(MainActivity.this, com.example.everquillapp.activities.JournalEditorActivity.class);
        startActivity(intent);
    }
    
    @Override
    public void onJournalClick(Journal journal) {
        Intent intent = new Intent(MainActivity.this, com.example.everquillapp.activities.JournalEditorActivity.class);
        intent.putExtra("journal_id", journal.getId());
        startActivity(intent);
    }
    
    @Override
    public void onJournalLongClick(Journal journal) {
        showJournalOptions(journal);
    }
    
    private void showJournalOptions(Journal journal) {
        String[] options = {"Edit", "Delete", "View AI History"};
        
        new AlertDialog.Builder(this)
                .setTitle(journal.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            Toast.makeText(MainActivity.this, "Edit - Coming soon", Toast.LENGTH_SHORT).show();
                            break;
                        case 1: // Delete
                            confirmDeleteJournal(journal);
                            break;
                        case 2: // AI History
                            Toast.makeText(MainActivity.this, "AI History - Coming soon", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }
    
    private void confirmDeleteJournal(Journal journal) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Journal")
                .setMessage("Are you sure you want to delete this journal?")
                .setPositiveButton("Delete", (dialog, which) -> deleteJournal(journal))
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void deleteJournal(Journal journal) {
        apiService.deleteJournal(journal.getId()).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    journalList.remove(journal);
                    adapter.notifyDataSetChanged();
                    updateEmptyView();
                    Toast.makeText(MainActivity.this, "Journal deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update bottom nav selection when returning from other screens
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_journals);
        }
        // Reload journals
        loadJournals();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            performLogout();
            return true;
        } else if (id == R.id.action_notifications) {
            Intent intent = new Intent(MainActivity.this, com.example.everquillapp.activities.NotificationActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(MainActivity.this, com.example.everquillapp.activities.ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_dashboard) {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_reviews) {
            Intent intent = new Intent(MainActivity.this, ReviewsActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void performLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    tokenManager.clearAll();
                    navigateToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
