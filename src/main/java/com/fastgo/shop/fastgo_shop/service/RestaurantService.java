package com.fastgo.shop.fastgo_shop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.fastgo.shop.fastgo_shop.domain.Restaurant;
import com.fastgo.shop.fastgo_shop.dto.RestaurantDto;
import com.fastgo.shop.fastgo_shop.dto.ShopKeeperDto;
import com.fastgo.shop.fastgo_shop.dto.UserCoordinatesDto;
import com.fastgo.shop.fastgo_shop.repositories.RestaurantRepository;

@Service
public class RestaurantService {
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    public boolean restaurantExists(String restaurantId) {
        return restaurantRepository.existsById(restaurantId);
    }

    public boolean saveRestaurant(ShopKeeperDto shopKeeperDto) {
        
        try
        {
            Restaurant restaurant = shopKeeperDtoToDomain(shopKeeperDto);
            restaurantRepository.save(restaurant);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public ResponseEntity<?> updateRestaurant(RestaurantDto restaurantDto, String idShopKeeper) {
        try{
            Optional<Restaurant> optionalRestaurant = restaurantRepository.findByShopkeeperId(idShopKeeper);
            
            if (!optionalRestaurant.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if(!optionalRestaurant.get().getId().equals(restaurantDto.getId())) {
                return new ResponseEntity<>("Unauthorized to update this restaurant", HttpStatus.UNAUTHORIZED);
            }

            Restaurant restaurantToUpdate = optionalRestaurant.get();
            restaurantToUpdate.setRestaurantName(restaurantDto.getRestaurantName());
            restaurantToUpdate.setRestaurantCity(restaurantDto.getRestaurantCity());    
            restaurantToUpdate.setRestaurantProvince(restaurantDto.getRestaurantProvince());
            restaurantToUpdate.setRestaurantAddress(restaurantDto.getRestaurantAddress());
            restaurantToUpdate.setRestaurantPostalCode(restaurantDto.getRestaurantPostalCode());
            restaurantToUpdate.setRestaurantVatNumber(restaurantDto.getRestaurantVatNumber());
            restaurantToUpdate.setPicture(restaurantDto.getPicture());
            
            try {
                double longitude = Double.parseDouble(restaurantDto.getLongitude());
                double latitude = Double.parseDouble(restaurantDto.getLatitude());
                restaurantToUpdate.setLocation(new GeoJsonPoint(longitude, latitude));
            } catch (NumberFormatException | NullPointerException e) {
                restaurantToUpdate.setLocation(null);
            }

            restaurantRepository.save(restaurantToUpdate);

            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error updating restaurant");
        }
    }

    public String getIdShopKeeper(String idShop){
        Optional <Restaurant> restaurant = restaurantRepository.findById(idShop);
        if (restaurant.isPresent()) {
            return restaurant.get().getShopkeeperId();
        }
        return null;
    }

    public Restaurant shopKeeperDtoToDomain(ShopKeeperDto shopKeeperDto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setShopkeeperId(shopKeeperDto.getId());
        restaurant.setRestaurantName(shopKeeperDto.getRestaurantName());
        restaurant.setRestaurantCity(shopKeeperDto.getRestaurantCity());
        restaurant.setRestaurantProvince(shopKeeperDto.getRestaurantProvince());
        restaurant.setRestaurantAddress(shopKeeperDto.getRestaurantAddress());
        restaurant.setRestaurantPostalCode(shopKeeperDto.getRestaurantPostalCode());
        restaurant.setRestaurantVatNumber(shopKeeperDto.getRestaurantVatNumber());
        restaurant.setRestaurantPhoneNumber(shopKeeperDto.getRestaurantPhoneNumber());

        try {
            double longitude = Double.parseDouble(shopKeeperDto.getLongitude());
            double latitude = Double.parseDouble(shopKeeperDto.getLatitude());
            restaurant.setLocation(new GeoJsonPoint(longitude, latitude));
        } catch (NumberFormatException | NullPointerException e) {
            restaurant.setLocation(null);
        }
        
        return restaurant;
    }

    public Restaurant restaurantDtoToDomain(RestaurantDto restaurantDto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantDto.getId());
        restaurant.setShopkeeperId(restaurantDto.getShopkeeperId());
        restaurant.setRestaurantName(restaurantDto.getRestaurantName());
        restaurant.setRestaurantCity(restaurantDto.getRestaurantCity());
        restaurant.setRestaurantProvince(restaurantDto.getRestaurantProvince());
        restaurant.setRestaurantAddress(restaurantDto.getRestaurantAddress());
        restaurant.setRestaurantPostalCode(restaurantDto.getRestaurantPostalCode());
        restaurant.setRestaurantVatNumber(restaurantDto.getRestaurantVatNumber());
        restaurant.setRestaurantPhoneNumber(restaurantDto.getRestaurantPhoneNumber());
        restaurant.setPicture(restaurantDto.getPicture());

        try {
            double longitude = Double.parseDouble(restaurantDto.getLongitude());
            double latitude = Double.parseDouble(restaurantDto.getLatitude());
            restaurant.setLocation(new GeoJsonPoint(longitude, latitude));
        } catch (NumberFormatException | NullPointerException e) {
            restaurant.setLocation(null);
        }
        
        return restaurant;
    }

    private RestaurantDto toDto(Restaurant entity) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(entity.getId());
        dto.setShopkeeperId(entity.getShopkeeperId());
        dto.setRestaurantName(entity.getRestaurantName());
        dto.setRestaurantCity(entity.getRestaurantCity());
        dto.setRestaurantProvince(entity.getRestaurantProvince());
        dto.setRestaurantAddress(entity.getRestaurantAddress());
        dto.setRestaurantPostalCode(entity.getRestaurantPostalCode());
        dto.setRestaurantVatNumber(entity.getRestaurantVatNumber());
        dto.setRestaurantPhoneNumber(entity.getRestaurantPhoneNumber());
        dto.setPicture(entity.getPicture());
        
        if (entity.getLocation() != null) {
            dto.setLongitude(String.valueOf(entity.getLocation().getX()));
            dto.setLatitude(String.valueOf(entity.getLocation().getY()));
        }
        
        return dto;
    }

    public List<RestaurantDto> findNearbyRestaurants(UserCoordinatesDto userCoords) {
        
        Point userPoint = new Point(userCoords.getLongitude(), userCoords.getLatitude());
        Distance maxDistance = new Distance(userCoords.getRangeInKm(), Metrics.KILOMETERS);

        List<Restaurant> nearbyRestaurants = restaurantRepository.findByLocationNear(userPoint, maxDistance);

        return nearbyRestaurants.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RestaurantDto findRestaurantById(String id) {
        
        Optional <Restaurant> reOptional = restaurantRepository.findById(id);
        if (reOptional.isPresent()) {
            return toDto(reOptional.get());      
        }
        return null;
    }

    public RestaurantDto findRestaurantByShopkeeperId(String idShopKeeper) {
        
        Optional <Restaurant> reOptional = restaurantRepository.findByShopkeeperId(idShopKeeper);
        if (reOptional.isPresent()) {
            return toDto(reOptional.get());      
        }
        return null;
    }
}