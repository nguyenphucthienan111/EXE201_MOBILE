package com.example.everquillapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Journal {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("userId")
    private String userId;
    
    private String title;
    private String content;
    
    @SerializedName("richContent")
    private String richContent;
    
    @SerializedName("templateId")
    private String templateId;
    
    @SerializedName("templateName")
    private String templateName;
    
    private String mood;
    private List<String> tags;
    private String suggestion;
    
    @SerializedName("cloudSynced")
    private boolean cloudSynced;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public Journal() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRichContent() {
        return richContent;
    }

    public void setRichContent(String richContent) {
        this.richContent = richContent;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public boolean isCloudSynced() {
        return cloudSynced;
    }

    public void setCloudSynced(boolean cloudSynced) {
        this.cloudSynced = cloudSynced;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

