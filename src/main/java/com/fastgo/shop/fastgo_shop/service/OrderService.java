package com.fastgo.shop.fastgo_shop.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastgo.shop.fastgo_shop.config.MqttConfig;
import com.fastgo.shop.fastgo_shop.domain.Order;
import com.fastgo.shop.fastgo_shop.dto.OrderDto;
import com.fastgo.shop.fastgo_shop.repositories.OrderRepository;
import com.fastgo.shop.fastgo_shop.security.JwtUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MqttConfig.MqttGateway mqttGateway;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtilities jwtUtilities;

    /**
     * Gestisce l'intero flusso di creazione ordine:
     * 1. Mappa DTO -> Entity
     * 2. Salva su MongoDB (generando ID)
     * 3. Pubblica su MQTT (usando l'ID generato)
     * 4. Ritorna il DTO aggiornato
     */
    public OrderDto processAndCreateOrder(OrderDto orderDto) {
        try {
            // 1. Mappatura
            Order orderEntity = toEntity(orderDto);
            
            // Imposta stato iniziale se nullo
            if (orderEntity.getOrderStatus() == null) {
                orderEntity.setOrderStatus("PENDING");
            }

            // 2. Salvataggio DB (MongoDB genera l'ID qui)
            Order savedOrder = orderRepository.save(orderEntity);
            logger.info("Ordine salvato su DB con ID: {}", savedOrder.getId());

            // Riconvertiamo in DTO per avere l'ID generato e inviarlo via MQTT
            OrderDto savedDto = toDto(savedOrder);

            // 3. Invio MQTT
            String orderJson = objectMapper.writeValueAsString(savedDto);
            
            // Topic specifico per l'ordine: shop/{shopId}/{orderId}
            // Questo permette persistenza granulare e QoS 2
            String topic = "shop/" + savedDto.getShopId() + "/" + savedDto.getId();
            
            logger.info("Pubblicazione MQTT su topic: {}", topic);
            mqttGateway.sendToMqtt(orderJson, topic);

            return savedDto;

        } catch (Exception e) {
            logger.error("Errore nel service durante creazione ordine", e);
            throw new RuntimeException("Errore processamento ordine: " + e.getMessage());
        }
    }

    public List<OrderDto> getOrdersByClientId(String clientId) {
        Optional<List<Order>> orders = orderRepository.findByClientId(clientId);
        if (orders.isPresent()) {
            return orders.get().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    public OrderDto updateOrderStatus(String orderId, String newStatus) throws Exception {
        // 1. Recupera l'ordine
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        // 2. Aggiorna lo stato
        order.setOrderStatus(newStatus);
        
        // 3. Salva su DB
        Order savedOrder = orderRepository.save(order);
        logger.info("Stato ordine {} aggiornato a {}", orderId, newStatus);

        // 4. Converti in DTO
        OrderDto savedDto = toDto(savedOrder);

        // 5. PUBBLICA AGGIORNAMENTO SU MQTT (Cruciale per il real-time)
        // Usiamo lo stesso topic specifico: shop/{shopId}/{orderId}
        String orderJson = objectMapper.writeValueAsString(savedDto);
        String topic = "shop/" + savedDto.getShopId() + "/" + savedDto.getId();
        
        logger.info("Invio aggiornamento stato MQTT su: {}", topic);
        mqttGateway.sendToMqtt(orderJson, topic);

        return savedDto;
    }

    // --- HELPER MAPPING FUNCTIONS ---

    public Order toEntity(OrderDto dto) {
        if (dto == null) return null;

        Order order = new Order();
        if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
            order.setId(dto.getId());
        } else {
            order.setId(null); 
        }
        order.setClientId(dto.getClientId());
        order.setUsernameClient(dto.getUsernameClient());
        order.setShopId(dto.getShopId());
        order.setShopName(dto.getShopName());
        order.setTotalPrice(dto.getTotalPrice());
        order.setOrderDate(dto.getOrderDate());
        order.setOrderStatus(dto.getOrderStatus());

        if (dto.getShopAddress() != null) {
            order.setShopAddress(new Order.Address(
                dto.getShopAddress().getStreet(),
                dto.getShopAddress().getCity(),
                dto.getShopAddress().getZipCode()
            ));
        }

        if (dto.getDeliveryAddress() != null) {
            order.setDeliveryAddress(new Order.Address(
                dto.getDeliveryAddress().getStreet(),
                dto.getDeliveryAddress().getCity(),
                dto.getDeliveryAddress().getZipCode()
            ));
        }

        if (dto.getOrderDetails() != null) {
            List<Order.OrderDetails> details = dto.getOrderDetails().stream()
                .map(d -> new Order.OrderDetails(d.getProductName(), d.getQuantity(), d.getPriceProduct()))
                .collect(Collectors.toList());
            order.setOrderDetails(details);
        }

        return order;
    }

    public OrderDto toDto(Order entity) {
        if (entity == null) return null;

        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClientId());
        dto.setUsernameClient(entity.getUsernameClient());
        dto.setShopId(entity.getShopId());
        dto.setShopName(entity.getShopName());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setOrderDate(entity.getOrderDate());
        dto.setOrderStatus(entity.getOrderStatus());

        if (entity.getShopAddress() != null) {
            OrderDto.AddressDto ad = new OrderDto.AddressDto();
            ad.setStreet(entity.getShopAddress().getStreet());
            ad.setCity(entity.getShopAddress().getCity());
            ad.setZipCode(entity.getShopAddress().getZipCode());
            dto.setShopAddress(ad);
        }

        if (entity.getDeliveryAddress() != null) {
            OrderDto.AddressDto ad = new OrderDto.AddressDto();
            ad.setStreet(entity.getDeliveryAddress().getStreet());
            ad.setCity(entity.getDeliveryAddress().getCity());
            ad.setZipCode(entity.getDeliveryAddress().getZipCode());
            dto.setDeliveryAddress(ad);
        }

        if (entity.getOrderDetails() != null) {
            List<OrderDto.OrderDetailsDto> details = entity.getOrderDetails().stream()
                .map(d -> {
                    OrderDto.OrderDetailsDto odd = new OrderDto.OrderDetailsDto();
                    odd.setProductName(d.getProductName());
                    odd.setQuantity(d.getQuantity());
                    odd.setPriceProduct(d.getPriceProduct());
                    return odd;
                })
                .collect(Collectors.toList());
            dto.setOrderDetails(details);
        }

        return dto;
    }


    public String userId(String token){
        return jwtUtilities.extractUserId(token);
    }

    public boolean isTokenValid(String token){
        return jwtUtilities.hasRoleClient(token);
    }

    public boolean isTokenValidShop(String token){
        return jwtUtilities.hasRoleShop(token);
    }
}