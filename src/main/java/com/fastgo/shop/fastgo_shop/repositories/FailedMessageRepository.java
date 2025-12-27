package com.fastgo.shop.fastgo_shop.repositories;

import com.fastgo.shop.fastgo_shop.domain.FailedMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FailedMessageRepository extends MongoRepository<FailedMessage, String> {
    
    List<FailedMessage> findByStatus(String status);
}