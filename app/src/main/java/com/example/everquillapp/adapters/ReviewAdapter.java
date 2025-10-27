package com.example.everquillapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everquillapp.R;

import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    
    private final Context context;
    private final List<Map<String, Object>> reviews;

    public ReviewAdapter(Context context, List<Map<String, Object>> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Map<String, Object> review = reviews.get(position);
        
        Object ratingObj = review.get("rating");
        if (ratingObj instanceof Number) {
            holder.ratingBar.setRating(((Number) ratingObj).floatValue());
        }
        
        String comment = (String) review.get("comment");
        holder.txtComment.setText(comment != null ? comment : "");
        
        Map<String, Object> user = (Map<String, Object>) review.get("userId");
        if (user != null) {
            String userName = (String) user.get("name");
            holder.txtUserName.setText(userName != null ? userName : "Anonymous");
        } else {
            holder.txtUserName.setText("Anonymous");
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtUserName, txtComment;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            txtUserName = itemView.findViewById(R.id.txt_user_name);
            txtComment = itemView.findViewById(R.id.txt_comment);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}

