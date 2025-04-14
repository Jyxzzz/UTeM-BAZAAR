package com.utembazaar.ecommapp.models;

import com.google.firebase.database.PropertyName;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order {
    private String orderId;
    private String address;
    private String status;
    private double totalPrice;
    private long timestamp;
    private List<Item> items;
    private String uploaderId;
    private String uploaderName;
    private String paymentNonce;

    public Order() {}

    public Order(String orderId, String address, String status, double totalPrice, long timestamp, List<Item> items, String uploaderId, String uploaderName, String paymentNonce) {
        this.orderId = orderId;
        this.address = address;
        this.status = status;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
        this.items = items;
        this.uploaderId = uploaderId;
        this.uploaderName = uploaderName;
        this.paymentNonce = paymentNonce;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address != null ? address : "Unknown address";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status != null ? status : "Pending";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Item> getItems() {
        return items != null ? items : Collections.emptyList();
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        if (uploaderId == null || uploaderId.isEmpty()) {
            throw new IllegalArgumentException("Uploader ID cannot be null or empty");
        }
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName != null ? uploaderName : "Unknown uploader";
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getPaymentNonce() {
        return paymentNonce;
    }

    public void setPaymentNonce(String paymentNonce) {
        this.paymentNonce = paymentNonce;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", address='" + getAddress() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", totalPrice=" + totalPrice +
                ", timestamp=" + getFormattedTimestamp() +
                ", items=" + items +
                ", uploaderId='" + uploaderId + '\'' +
                ", uploaderName='" + getUploaderName() + '\'' +
                ", paymentNonce='" + paymentNonce + '\'' +
                '}';
    }
}
