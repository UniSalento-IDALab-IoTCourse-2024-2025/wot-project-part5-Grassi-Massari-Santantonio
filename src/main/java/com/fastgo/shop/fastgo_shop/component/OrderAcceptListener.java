package com.fastgo.shop.fastgo_shop.component;



import com.fastgo.shop.fastgo_shop.config.RabbitMqConfig;
import com.fastgo.shop.fastgo_shop.dto.OrderAcceptDto;
import com.fastgo.shop.fastgo_shop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class OrderAcceptListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderAcceptListener.class);

    @Autowired
    private OrderService orderService;

    @SendTo
    @RabbitListener(queues = RabbitMqConfig.ACCEPT_QUEUE)
    public String handleOrderAccept(OrderAcceptDto acceptDto) {
        logger.info("RPC Request: Rider {} vuole accettare ordine {}", 
                    acceptDto.getRiderId(), acceptDto.getOrderId());

        try {
            boolean updated = orderService.assignRiderToOrder(
                acceptDto.getOrderId(),
                acceptDto.getRiderId(),
                acceptDto.getRiderName()
            );

            if (updated) {
                return "OK"; 
            } else {
                return "ERROR_ALREADY_ASSIGNED";
            }

        } catch (Exception e) {
            logger.error("Errore assegnazione rider", e);
            return "ERROR_INTERNAL";
        }
    }
}