package com.fastgo.shop.fastgo_shop.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document(collection = "orders")
public class OrderBlockchain {
    
    @Id
    private String orderId;
    @NonNull
    private String riderId;
    @NonNull
    private String shopId;
    @NonNull
    private String clientId;
    @NonNull
    private String orderDate;
    @NonNull
    private double totalPrice;
    @NonNull
    private String result;
    private int points;

    private String transactionHash;
    private boolean confirmed;

    // Getters and Setters
    public String getOrderId() {
        return orderId; 
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId; 
    }
    public String getRiderId() {
        return riderId;
    }
    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }
    public String getShopId() {
        return shopId;}
    public void setShopId(String shopId) {
        this.shopId = shopId;}

    public String getClientId() {
        return clientId;}

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public String getTransactionHash() {
        return transactionHash;
    }
    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
    public boolean isConfirmed() {
        return confirmed;
    }
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

}
