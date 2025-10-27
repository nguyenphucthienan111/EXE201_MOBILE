package com.example.everquillapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.everquillapp.R;

import java.util.Map;

public class AIAnalysisDialog extends Dialog {
    
    private Map<String, Object> analysisData;
    private TextView txtSentiment, txtEmotion, txtKeywords, txtSummary, txtSuggestions;
    private Button btnClose;

    public AIAnalysisDialog(@NonNull Context context, Map<String, Object> analysisData) {
        super(context);
        this.analysisData = analysisData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ai_analysis);
        
        initViews();
        displayData();
        
        btnClose.setOnClickListener(v -> dismiss());
    }
    
    private void initViews() {
        txtSentiment = findViewById(R.id.txt_sentiment);
        txtEmotion = findViewById(R.id.txt_emotion);
        txtKeywords = findViewById(R.id.txt_keywords);
        txtSummary = findViewById(R.id.txt_summary);
        txtSuggestions = findViewById(R.id.txt_suggestions);
        btnClose = findViewById(R.id.btn_close);
    }
    
    private void displayData() {
        if (analysisData == null) return;
        
        // Extract sentiment analysis
        Map<String, Object> sentimentAnalysis = (Map<String, Object>) analysisData.get("sentimentAnalysis");
        if (sentimentAnalysis != null) {
            Object sentiment = sentimentAnalysis.get("overallSentiment");
            if (sentiment != null) {
                txtSentiment.setText("Sentiment: " + sentiment.toString());
            }
        }
        
        // Extract emotion analysis
        Map<String, Object> emotionAnalysis = (Map<String, Object>) analysisData.get("emotionAnalysis");
        if (emotionAnalysis != null) {
            Object emotion = emotionAnalysis.get("primaryEmotion");
            if (emotion != null) {
                txtEmotion.setText("Primary Emotion: " + emotion.toString());
            }
        }
        
        // Extract keywords
        Map<String, Object> keywords = (Map<String, Object>) analysisData.get("keywords");
        if (keywords != null) {
            Object emotional = keywords.get("emotional");
            if (emotional != null) {
                txtKeywords.setText("Keywords: " + emotional.toString());
            }
        }
        
        // Extract summary
        Object summary = analysisData.get("summary");
        if (summary != null) {
            txtSummary.setText(summary.toString());
        }
        
        // Extract suggestions
        Map<String, Object> suggestions = (Map<String, Object>) analysisData.get("improvementSuggestions");
        if (suggestions != null) {
            Object immediate = suggestions.get("immediate");
            if (immediate != null) {
                txtSuggestions.setText(immediate.toString());
            }
        }
    }
}

