package com.fastgo.shop.fastgo_shop.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private String clientId;
    private String usernameClient;
    private String shopId;
    private String shopName;
    private Address shopAddress;
    private Address deliveryAddress;
    private List<OrderDetails> orderDetails;
    private String orderDate;
    private String orderStatus;
    private double totalPrice;

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
    public Address getShopAddress() { return shopAddress; }
    public void setShopAddress(Address shopAddress) { this.shopAddress = shopAddress; }
    public Address getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(Address deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public List<OrderDetails> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetails> orderDetails) { this.orderDetails = orderDetails; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    // --- Classi Interne per il Dominio ---
    
    public static class Address {
        private String street;
        private String city;
        private String zipCode;

        // Costruttori, Getters, Setters
        public Address() {}
        public Address(String street, String city, String zipCode) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
        }
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }

    public static class OrderDetails {
        private String productName;
        private int quantity;
        private double priceProduct;

        // Costruttori, Getters, Setters
        public OrderDetails() {}
        public OrderDetails(String productName, int quantity, double priceProduct) {
            this.productName = productName;
            this.quantity = quantity;
            this.priceProduct = priceProduct;
        }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPriceProduct() { return priceProduct; }
        public void setPriceProduct(double priceProduct) { this.priceProduct = priceProduct; }
    }
}