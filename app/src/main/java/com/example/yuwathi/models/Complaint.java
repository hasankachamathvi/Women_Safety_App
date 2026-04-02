package com.example.yuwathi.models;

/**
 * Complaint Model Class
 * Represents a complaint submitted by a user
 */
public class Complaint {
    private String id;
    private String userId;
    private String title;
    private String location;
    private String date;
    private String status; // Pending, Under Review, Resolved
    private String priority; // Low, Medium, High
    private String description;
    private String witnesses;
    private String vehicle;
    private String suspectDescription;
    private boolean ongoing;
    private String contactPreference;

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

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getWitnesses() {
        return witnesses;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getSuspectDescription() {
        return suspectDescription;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public String getContactPreference() {
        return contactPreference;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWitnesses(String witnesses) {
        this.witnesses = witnesses;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public void setSuspectDescription(String suspectDescription) {
        this.suspectDescription = suspectDescription;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public void setContactPreference(String contactPreference) {
        this.contactPreference = contactPreference;
    }
}
