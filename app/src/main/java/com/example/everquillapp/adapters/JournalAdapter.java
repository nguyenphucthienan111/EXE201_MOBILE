package com.example.everquillapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everquillapp.R;
import com.example.everquillapp.models.Journal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    
    private final Context context;
    private final List<Journal> journals;
    private final OnJournalClickListener listener;

    public interface OnJournalClickListener {
        void onJournalClick(Journal journal);
        void onJournalLongClick(Journal journal);
    }

    public JournalAdapter(Context context, List<Journal> journals, OnJournalClickListener listener) {
        this.context = context;
        this.journals = journals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        Journal journal = journals.get(position);
        
        // Set title
        holder.txtTitle.setText(journal.getTitle() != null ? journal.getTitle() : "Untitled");
        
        // Set content preview (strip HTML tags)
        String content = journal.getContent();
        if (content != null && !content.isEmpty()) {
            String plainText = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString();
            String preview = plainText.length() > 150 ? plainText.substring(0, 150) + "..." : plainText;
            holder.txtContent.setText(preview);
        } else {
            holder.txtContent.setText("No content");
        }
        
        // Set mood
        if (journal.getMood() != null && !journal.getMood().isEmpty()) {
            holder.txtMood.setText(capitalizeFirst(journal.getMood()));
            holder.txtMood.setVisibility(View.VISIBLE);
            setMoodColor(holder.txtMood, journal.getMood());
        } else {
            holder.txtMood.setVisibility(View.GONE);
        }
        
        // Set date
        String dateStr = formatDate(journal.getUpdatedAt() != null ? journal.getUpdatedAt() : journal.getCreatedAt());
        holder.txtDate.setText(dateStr);
        
        // Set click listeners
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJournalClick(journal);
            }
        });
        
        holder.cardView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onJournalLongClick(journal);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtContent, txtMood, txtDate;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            txtTitle = itemView.findViewById(R.id.txt_journal_title);
            txtContent = itemView.findViewById(R.id.txt_journal_content);
            txtMood = itemView.findViewById(R.id.txt_journal_mood);
            txtDate = itemView.findViewById(R.id.txt_journal_date);
        }
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private void setMoodColor(TextView textView, String mood) {
        int colorRes = R.color.text_secondary;
        
        switch (mood.toLowerCase()) {
            case "happy":
                colorRes = R.color.mood_happy;
                break;
            case "sad":
                colorRes = R.color.mood_sad;
                break;
            case "angry":
                colorRes = R.color.mood_angry;
                break;
            case "anxious":
                colorRes = R.color.mood_anxious;
                break;
            case "calm":
                colorRes = R.color.mood_calm;
                break;
            case "excited":
                colorRes = R.color.mood_excited;
                break;
        }
        
        textView.setBackgroundColor(context.getResources().getColor(colorRes, null));
    }
    
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US);
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (Exception e) {
            return dateString;
        }
    }
}


