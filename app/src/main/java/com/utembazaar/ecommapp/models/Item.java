package com.utembazaar.ecommapp.models;

public class Item {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int quantity;
    private String uploaderId;
    private String uploaderName;
    private String uploaderEmail; // Add this field

    // Default constructor required for Firebase
    public Item() {}

    // Updated constructor to include uploaderEmail
    public Item(String id, String name, double price, String description, String imageUrl, String uploaderId, String uploaderName, String uploaderEmail) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.quantity = 1; // Default quantity is 1
        this.uploaderId = uploaderId;
        this.uploaderName = uploaderName;
        this.uploaderEmail = uploaderEmail; // Initialize uploaderEmail
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    // Getter and Setter for uploaderEmail
    public String getUploaderEmail() {
        return uploaderEmail;
    }

    public void setUploaderEmail(String uploaderEmail) {
        this.uploaderEmail = uploaderEmail;
    }
}