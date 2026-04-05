package com.example.yuwathi.models;

/**
 * Data model representing a safety tip item.
 */
public class SafetyTip {
    private String id;
    private String title;
    private String description;
    private String category;
    private String date;
    private boolean isVisible = true;

    public SafetyTip() {
    }

    public SafetyTip(String id, String title, String description, String category, boolean isVisible) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.isVisible = isVisible;
    }

    public SafetyTip(String id, String title, String description, String category, String date, boolean isVisible) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
        this.isVisible = isVisible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
