package com.example.everquillapp.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.everquillapp.R;
import com.example.everquillapp.dialogs.AIAnalysisDialog;
import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.Journal;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalEditorActivity extends AppCompatActivity {
    
    private EditText editTitle;
    private RichEditor editorContent;
    private Spinner spinnerMood;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private String journalId = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_editor);
        
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
        
        // Setup Rich Editor
        setupRichEditor();
        
        // Setup Mood Spinner
        setupMoodSpinner();
        
        // Check if editing existing journal
        journalId = getIntent().getStringExtra("journal_id");
        if (journalId != null && !journalId.isEmpty()) {
            isEditMode = true;
            setTitle("Edit Journal");
            loadJournal();
        } else {
            setTitle("Create Journal");
        }
    }
    
    private void initViews() {
        editTitle = findViewById(R.id.edit_title);
        editorContent = findViewById(R.id.editor_content);
        spinnerMood = findViewById(R.id.spinner_mood);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupRichEditor() {
        editorContent.setEditorHeight(200);
        editorContent.setEditorFontSize(16);
        editorContent.setPadding(10, 10, 10, 10);
        editorContent.setPlaceholder("Write your thoughts here...");
        
        // Setup formatting toolbar
        findViewById(R.id.action_bold).setOnClickListener(v -> editorContent.setBold());
        findViewById(R.id.action_italic).setOnClickListener(v -> editorContent.setItalic());
        findViewById(R.id.action_underline).setOnClickListener(v -> editorContent.setUnderline());
        findViewById(R.id.action_heading).setOnClickListener(v -> editorContent.setHeading(2));
        findViewById(R.id.action_bullet).setOnClickListener(v -> editorContent.setBullets());
    }
    
    private void setupMoodSpinner() {
        String[] moods = {"Happy", "Sad", "Angry", "Anxious", "Calm", "Excited", "Reflective", "Grateful"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMood.setAdapter(adapter);
    }
    
    private void loadJournal() {
        showLoading(true);
        
        apiService.getJournal(journalId).enqueue(new Callback<Journal>() {
            @Override
            public void onResponse(Call<Journal> call, Response<Journal> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    Journal journal = response.body();
                    populateFields(journal);
                } else {
                    Toast.makeText(JournalEditorActivity.this, "Journal not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            
            @Override
            public void onFailure(Call<Journal> call, Throwable t) {
                showLoading(false);
                Toast.makeText(JournalEditorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    private void populateFields(Journal journal) {
        if (journal.getTitle() != null) {
            editTitle.setText(journal.getTitle());
        }
        
        if (journal.getRichContent() != null && !journal.getRichContent().isEmpty()) {
            editorContent.setHtml(journal.getRichContent());
        } else if (journal.getContent() != null) {
            editorContent.setHtml(journal.getContent());
        }
        
        if (journal.getMood() != null) {
            setMoodSelection(journal.getMood());
        }
    }
    
    private void setMoodSelection(String mood) {
        ArrayAdapter adapter = (ArrayAdapter) spinnerMood.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(mood)) {
                spinnerMood.setSelection(i);
                break;
            }
        }
    }
    
    private void saveJournal() {
        String title = editTitle.getText().toString().trim();
        String content = editorContent.getHtml();
        String mood = spinnerMood.getSelectedItem().toString().toLowerCase();
        
        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }
        
        showLoading(true);
        
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("richContent", content);
        body.put("content", content); // Plain text fallback
        body.put("mood", mood);
        
        Call<ApiResponse<Journal>> call;
        if (isEditMode) {
            call = apiService.updateJournal(journalId, body);
        } else {
            call = apiService.createJournal(body);
        }
        
        call.enqueue(new Callback<ApiResponse<Journal>>() {
            @Override
            public void onResponse(Call<ApiResponse<Journal>> call, Response<ApiResponse<Journal>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Journal> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(JournalEditorActivity.this, 
                                isEditMode ? "Journal updated!" : "Journal created!", 
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(JournalEditorActivity.this, 
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to save", 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JournalEditorActivity.this, "Failed to save journal", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Journal>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(JournalEditorActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_save) {
            saveJournal();
            return true;
        } else if (id == R.id.action_analyze) {
            analyzeJournal();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void analyzeJournal() {
        if (journalId == null) {
            Toast.makeText(this, "Please save the journal first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        apiService.analyzeJournal(journalId).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Map<String, Object>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        showAnalysisResult(apiResponse.getData());
                    } else {
                        Toast.makeText(JournalEditorActivity.this, "Analysis failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JournalEditorActivity.this, "Failed to analyze", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(JournalEditorActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showAnalysisResult(Map<String, Object> result) {
        AIAnalysisDialog dialog = new AIAnalysisDialog(this, result);
        dialog.show();
    }
    
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard changes?")
                .setMessage("You have unsaved changes. Do you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
    }
}

