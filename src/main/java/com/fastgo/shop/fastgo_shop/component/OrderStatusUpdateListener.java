package com.fastgo.shop.fastgo_shop.component;

import com.fastgo.shop.fastgo_shop.config.RabbitMqConfig;
import com.fastgo.shop.fastgo_shop.dto.OrderDto;
import com.fastgo.shop.fastgo_shop.dto.OrderStatusUpdateDto;
import com.fastgo.shop.fastgo_shop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusUpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusUpdateListener.class);

    @Autowired
    private OrderService orderService;

    @SendTo // Fondamentale per il pattern RPC: manda la risposta indietro
    @RabbitListener(queues = RabbitMqConfig.UPDATE_STATUS_QUEUE)
    public String handleStatusUpdate(OrderStatusUpdateDto updateDto) {
        logger.info("RPC Request: Aggiornamento stato ordine {} a {}", 
                    updateDto.getOrderId(), updateDto.getNewStatus());

        try {
            OrderDto updated = orderService.updateOrderStatus(
                updateDto.getOrderId(),
                updateDto.getNewStatus()
            );

            if (updated != null) {
                return "OK"; 
            } else {
                return "ERROR_UPDATE_FAILED";
            }

        } catch (Exception e) {
            logger.error("Errore aggiornamento stato ordine", e);
            return "ERROR_INTERNAL";
        }
    }
}