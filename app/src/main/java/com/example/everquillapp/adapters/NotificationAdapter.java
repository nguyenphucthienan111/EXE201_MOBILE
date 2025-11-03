package com.example.everquillapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everquillapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    
    private final Context context;
    private final List<Map<String, Object>> notifications;

    public NotificationAdapter(Context context, List<Map<String, Object>> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Map<String, Object> notification = notifications.get(position);
        
        String title = (String) notification.get("title");
        String message = (String) notification.get("message");
        String createdAt = (String) notification.get("createdAt");
        Boolean isRead = (Boolean) notification.get("isRead");
        
        holder.txtTitle.setText(title != null ? title : "Notification");
        holder.txtMessage.setText(message != null ? message : "");
        holder.txtDate.setText(formatDate(createdAt));
        
        // Highlight unread notifications
        if (isRead != null && !isRead) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light, null));
            holder.txtTitle.setTextColor(context.getResources().getColor(R.color.white, null));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.surface, null));
            holder.txtTitle.setTextColor(context.getResources().getColor(R.color.text_primary, null));
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtMessage, txtDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            txtTitle = itemView.findViewById(R.id.txt_notification_title);
            txtMessage = itemView.findViewById(R.id.txt_notification_message);
            txtDate = itemView.findViewById(R.id.txt_notification_date);
        }
    }
    
    private String formatDate(String dateString) {
        if (dateString == null) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, h:mm a", Locale.US);
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (Exception e) {
            return dateString;
        }
    }
}


