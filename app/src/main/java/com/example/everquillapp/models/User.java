package com.example.everquillapp.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;
    
    private String name;
    private String email;
    private String avatar;
    private String plan;
    
    @SerializedName("premiumExpiresAt")
    private String premiumExpiresAt;
    
    private boolean verified;
    
    @SerializedName("createdAt")
    private String createdAt;

    // Constructors
    public User() {}

    public User(String id, String name, String email, String avatar, String plan) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.plan = plan;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPremiumExpiresAt() {
        return premiumExpiresAt;
    }

    public void setPremiumExpiresAt(String premiumExpiresAt) {
        this.premiumExpiresAt = premiumExpiresAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isPremium() {
        return "premium".equalsIgnoreCase(plan);
    }
}


