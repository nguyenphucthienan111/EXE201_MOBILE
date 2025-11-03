package com.example.everquillapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.everquillapp.R;
import com.example.everquillapp.models.Template;

import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {
    
    private final Context context;
    private final List<Template> templates;
    private final OnTemplateClickListener listener;

    public interface OnTemplateClickListener {
        void onTemplateClick(Template template);
    }

    public TemplateAdapter(Context context, List<Template> templates, OnTemplateClickListener listener) {
        this.context = context;
        this.templates = templates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        Template template = templates.get(position);
        
        holder.txtName.setText(template.getName());
        
        // Load thumbnail
        if (template.getThumbnailUrl() != null && !template.getThumbnailUrl().isEmpty()) {
            Glide.with(context)
                    .load(template.getThumbnailUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imgThumbnail);
        }
        
        // Show premium badge
        if (template.isPremium()) {
            holder.txtPremiumBadge.setVisibility(View.VISIBLE);
        } else {
            holder.txtPremiumBadge.setVisibility(View.GONE);
        }
        
        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTemplateClick(template);
            }
        });
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    static class TemplateViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgThumbnail;
        TextView txtName, txtPremiumBadge;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            imgThumbnail = itemView.findViewById(R.id.img_template_thumbnail);
            txtName = itemView.findViewById(R.id.txt_template_name);
            txtPremiumBadge = itemView.findViewById(R.id.txt_premium_badge);
        }
    }
}


