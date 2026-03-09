package com.example.yuwathi.models;

/**
 * Safety Tip Model Class
 * Represents a safety tip in the system
 */
public class SafetyTip {
    private String id;
    private String title;
    private String description;
    private String category;
    private boolean isVisible;

    public SafetyTip() {
    }

    public SafetyTip(String id, String title, String description, String category, boolean isVisible) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.isVisible = isVisible;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public boolean isVisible() {
        return isVisible;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
