package com.fastgo.shop.fastgo_shop.repositories;


import com.fastgo.shop.fastgo_shop.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByShopId(String shopId);
    Optional<List<Order>> findByClientId(String clientId);
}