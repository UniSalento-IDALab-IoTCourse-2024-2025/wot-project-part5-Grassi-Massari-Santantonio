package com.fastgo.shop.fastgo_shop.dto;


import java.util.List;

public class OrderDto {

    private String id;
    private String clientId;
    private String usernameClient;
    private String shopId;
    private String shopName;
    private AddressDto shopAddress;
    private AddressDto deliveryAddress;
    private List<OrderDetailsDto> orderDetails;
    private String orderDate;
    private String orderStatus;
    private double totalPrice;
     private String riderId;
    private String riderName;

    // Costruttore vuoto
    public OrderDto() {}

    // --- Getters e Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUsernameClient() { return usernameClient; }
    public void setUsernameClient(String usernameClient) { this.usernameClient = usernameClient; }

    public String getShopId() { return shopId; }
    public void setShopId(String shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public AddressDto getShopAddress() { return shopAddress; }
    public void setShopAddress(AddressDto shopAddress) { this.shopAddress = shopAddress; }

    public AddressDto getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(AddressDto deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public List<OrderDetailsDto> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailsDto> orderDetails) { this.orderDetails = orderDetails; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }



    public static class AddressDto {
        private String street;
        private String city;
        private String zipCode;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }

    public static class OrderDetailsDto {
        private String productName;
        private int quantity;
        private double priceProduct;

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPriceProduct() { return priceProduct; }
        public void setPriceProduct(double priceProduct) { this.priceProduct = priceProduct; }
    }
}