package com.fastgo.shop.fastgo_shop.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

@Document("restaurants")
public class Restaurant {
    
    @Id
    private String id;
    private String shopkeeperId;
    private String restaurantName;
    private String restaurantCity;  
    private String restaurantProvince;
    private String restaurantAddress; 
    private String restaurantPostalCode;
    private String restaurantVatNumber;
    //private String latitude;
    //private String longitude;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String restaurantPhoneNumber;

    private String pictureUrl;



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

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
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
