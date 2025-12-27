package com.fastgo.shop.fastgo_shop.dto;


public class RestaurantDto {

    private String id;
    private String shopkeeperId;
    private String restaurantName;
    private String restaurantCity;
    private String restaurantProvince;
    private String restaurantAddress;
    private String restaurantPostalCode;
    private String restaurantVatNumber;
    private String latitude;
    private String longitude;
    private String restaurantPhoneNumber;
    private String pictureUrl;

    public RestaurantDto() {
    }

    // Getters e Setters per tutti i campi
   
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getShopkeeperId() {
        return shopkeeperId;
    }
    public void setShopkeeperId(String shopkeeperId) {
        this.shopkeeperId = shopkeeperId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public String getRestaurantCity() {
        return restaurantCity;
    }
    public void setRestaurantCity(String restaurantCity) {
        this.restaurantCity = restaurantCity;
    }
    public String getRestaurantProvince() {
        return restaurantProvince;
    }
    public void setRestaurantProvince(String restaurantProvince) {
        this.restaurantProvince = restaurantProvince;
    }
    public String getRestaurantAddress() {
        return restaurantAddress;
    }
    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }
    public String getRestaurantPostalCode() {
        return restaurantPostalCode;
    }
    public void setRestaurantPostalCode(String restaurantPostalCode) {
        this.restaurantPostalCode = restaurantPostalCode;
    }
    public String getRestaurantVatNumber() {
        return restaurantVatNumber;
    }
    public void setRestaurantVatNumber(String restaurantVatNumber) {
        this.restaurantVatNumber = restaurantVatNumber;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getRestaurantPhoneNumber() {
        return restaurantPhoneNumber;
    }
    public void setRestaurantPhoneNumber(String restaurantPhoneNumber) {
        this.restaurantPhoneNumber = restaurantPhoneNumber;
    }

     public String getPicture() {
        return pictureUrl;
    }
    public void setPicture(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

}