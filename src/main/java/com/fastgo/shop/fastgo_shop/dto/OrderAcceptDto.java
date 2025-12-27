package com.fastgo.shop.fastgo_shop.dto;


import java.io.Serializable;

public class OrderAcceptDto implements Serializable {
    private String orderId;
    private String riderId;
    private String riderName;
    private String vehicleType;

    // Costruttori
    public OrderAcceptDto() {}

    public OrderAcceptDto(String orderId, String riderId, String riderName, String vehicleType) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.riderName = riderName;
        this.vehicleType = vehicleType;
    }

    // Getters e Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}