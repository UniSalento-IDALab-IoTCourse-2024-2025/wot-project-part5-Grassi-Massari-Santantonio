package com.fastgo.shop.fastgo_shop.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastgo.shop.fastgo_shop.config.RabbitMqConfig;
import com.fastgo.shop.fastgo_shop.domain.FailedMessage;
import com.fastgo.shop.fastgo_shop.dto.OrderDto;
import com.fastgo.shop.fastgo_shop.repositories.FailedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class OrderPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrderPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FailedMessageRepository failedMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendOrderToRider(OrderDto orderDto) {
        String orderId = orderDto.getId();
        

        String orderJson;
        try {
            orderJson = objectMapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            logger.error("Errore serializzazione JSON ordine {}", orderId, e);
            return;
        }

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        logger.info("Tentativo invio ordine {} a RabbitMQ...", orderId);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.ORDER_EXCHANGE,
                    RabbitMqConfig.ROUTING_KEY_ORDER,
                    orderDto,
                    correlationData
            );

         
            CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);

            if (confirm.isAck()) {
                logger.info(">>> RABBITMQ ACK: Ordine {} consegnato.", orderId);
            } else {
                handleFailure(orderId, orderJson, "NACK received: " + confirm.getReason());
            }

        } catch (Exception e) {
            handleFailure(orderId, orderJson, "Exception: " + e.getMessage());
    
        }
    }


    private void handleFailure(String orderId, String jsonPayload, String reason) {
        logger.error("!!! INVIO FALLITO per ordine {}. Salvataggio in FailedMessages. Motivo: {}", orderId, reason);
        
        FailedMessage failed = new FailedMessage(
            orderId,
            jsonPayload,
            RabbitMqConfig.ROUTING_KEY_ORDER,
            reason
        );
        failedMessageRepository.save(failed);
    }
}