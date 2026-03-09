package com.example.yuwathi.models;

/**
 * Complaint Model Class
 * Represents a complaint submitted by a user
 */
public class Complaint {
    private String id;
    private String title;
    private String location;
    private String date;
    private String status; // Pending, Under Review, Resolved
    private String priority; // Low, Medium, High

    public Complaint() {
    }

    public Complaint(String id, String title, String location, String date, String status, String priority) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.status = status;
        this.priority = priority;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
