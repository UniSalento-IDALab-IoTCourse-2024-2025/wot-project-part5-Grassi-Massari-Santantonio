package com.fastgo.shop.fastgo_shop.component;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RabbitMqRetryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqRetryScheduler.class);

    @Autowired
    private FailedMessageRepository failedMessageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Esegue ogni 60 secondi (60000 ms)
    @Scheduled(fixedDelay = 60000)
    public void retryFailedMessages() {
        List<FailedMessage> pendingMessages = failedMessageRepository.findByStatus("PENDING");

        if (pendingMessages.isEmpty()) return;

        logger.info("Trovati {} messaggi falliti da ritentare...", pendingMessages.size());

        for (FailedMessage msg : pendingMessages) {
            
            // Se ha fallito più di 10 volte, lo marchiamo come DEAD per smettere di provare
            if (msg.getRetryCount() >= 10) {
                msg.setStatus("DEAD");
                failedMessageRepository.save(msg);
                logger.error("Messaggio {} marcato come DEAD dopo troppi tentativi.", msg.getId());
                continue;
            }

            try {
                // Riconvertiamo il JSON in Oggetto
                OrderDto dto = objectMapper.readValue(msg.getJsonPayload(), OrderDto.class);
                
                CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
                
                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.ORDER_EXCHANGE,
                        msg.getTopic(), 
                        dto,
                        correlationData
                );

                CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);

                if (confirm.isAck()) {
                    logger.info("RETRY SUCCESSO: Messaggio {} inviato.", msg.getId());
                  
                    msg.setStatus("SENT");
                    failedMessageRepository.save(msg);
                } else {
                    incrementRetry(msg, "NACK on retry");
                }

            } catch (Exception e) {
                incrementRetry(msg, e.getMessage());
            }
        }
    }

    private void incrementRetry(FailedMessage msg, String error) {
        msg.setRetryCount(msg.getRetryCount() + 1);
        msg.setLastRetry(LocalDateTime.now());
        msg.setErrorMessage(error);
        failedMessageRepository.save(msg);
        logger.warn("Retry fallito per msg {}. Tentativo {}/10", msg.getId(), msg.getRetryCount());
    }
}