package com.fastgo.shop.fastgo_shop.dto;


import java.io.Serializable;

public class OrderStatusUpdateDto implements Serializable {
    private String orderId;
    private String newStatus;

    public OrderStatusUpdateDto() {}

    public OrderStatusUpdateDto(String orderId, String newStatus) {
        this.orderId = orderId;
        this.newStatus = newStatus;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}