package com.fastgo.shop.fastgo_shop.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastgo.shop.fastgo_shop.dto.*;
import com.fastgo.shop.fastgo_shop.service.SyncService;



@Component
public class SyncListener {

    @Autowired
    private SyncService syncService;


    private static final Logger log = LoggerFactory.getLogger(SyncListener.class);

   
    @RabbitListener(queues = "shop.sync.request.queue")
    public String handleRiderSyncRequest(SyncShopDto syncShopDto) {
        
      
        log.info("Richiesta di sincronizzazione ricevuta per: {}", syncShopDto.toString());

        if (!syncService.isTokenValidForShopkeeper(syncShopDto.getToken())) {
            return "Invalid token for shopkeeper";
        }

        return syncService.saveShopKeeper(syncShopDto.getShop());
    }
}