package com.example.everquillapp.models;

import com.google.gson.annotations.SerializedName;

public class Template {
    @SerializedName("_id")
    private String id;
    
    private String name;
    private String description;
    private String category; // "default", "premium", "user"
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;
    
    @SerializedName("usageCount")
    private int usageCount;
    
    @SerializedName("uploadedBy")
    private String uploadedBy;
    
    @SerializedName("createdAt")
    private String createdAt;

    // Constructors
    public Template() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isPremium() {
        return "premium".equalsIgnoreCase(category);
    }
}

