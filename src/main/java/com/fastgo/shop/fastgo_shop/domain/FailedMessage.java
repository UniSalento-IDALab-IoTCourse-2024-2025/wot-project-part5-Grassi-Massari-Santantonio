package com.fastgo.shop.fastgo_shop.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "failed_messages")
public class FailedMessage {

    @Id
    private String id;
    private String originalOrderId;
    private String jsonPayload; // L'OrderDto convertito in stringa
    private String topic;
    private String errorMessage;
    private int retryCount;
    private LocalDateTime lastRetry;
    private String status; // "PENDING", "SENT", "DEAD"

    // Costruttore vuoto
    public FailedMessage() {}

    public FailedMessage(String originalOrderId, String jsonPayload, String topic, String errorMessage) {
        this.originalOrderId = originalOrderId;
        this.jsonPayload = jsonPayload;
        this.topic = topic;
        this.errorMessage = errorMessage;
        this.retryCount = 0;
        this.lastRetry = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOriginalOrderId() { return originalOrderId; }
    public void setOriginalOrderId(String originalOrderId) { this.originalOrderId = originalOrderId; }
    public String getJsonPayload() { return jsonPayload; }
    public void setJsonPayload(String jsonPayload) { this.jsonPayload = jsonPayload; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public LocalDateTime getLastRetry() { return lastRetry; }
    public void setLastRetry(LocalDateTime lastRetry) { this.lastRetry = lastRetry; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}