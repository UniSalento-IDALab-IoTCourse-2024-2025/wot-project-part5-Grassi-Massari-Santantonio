package com.fastgo.shop.fastgo_shop.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastgo.shop.fastgo_shop.component.OrderPublisher;
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

    @Autowired
    private OrderPublisher orderPublisher;

   
    public OrderDto processAndCreateOrder(OrderDto orderDto) {
        try {
            
            Order orderEntity = toEntity(orderDto);
            
            
            if (orderEntity.getOrderStatus() == null) {
                orderEntity.setOrderStatus("PENDING");
            }

            
            Order savedOrder = orderRepository.save(orderEntity);
            logger.info("Ordine salvato su DB con ID: {}", savedOrder.getId());

            
            OrderDto savedDto = toDto(savedOrder);

            
            String orderJson = objectMapper.writeValueAsString(savedDto);
            
            // Topic specifico per l'ordine: shop/{shopId}/{orderId}
            
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
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        
        order.setOrderStatus(newStatus);
        
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Stato ordine {} aggiornato a {}", orderId, newStatus);

        
        OrderDto savedDto = toDto(savedOrder);

        //  AGGIORNAMENTO SU MQTT 
        //  topic specifico: shop/{shopId}/{orderId}
        String orderJson = objectMapper.writeValueAsString(savedDto);
        String topic = "shop/" + savedDto.getShopId() + "/" + savedDto.getId();
        
        logger.info("Invio aggiornamento stato MQTT su: {}", topic);
        mqttGateway.sendToMqtt(orderJson, topic);

        if("CONFIRMED".equalsIgnoreCase(newStatus) || "ACCEPTED".equalsIgnoreCase(newStatus)) {
            // Invia ordine a RabbitMQ per il rider
           try {
                orderPublisher.sendOrderToRider(savedDto);
            } catch (Exception e) {
               
                logger.error("Errore invio RabbitMQ", e);
            }

        }
        return savedDto;
    }


    public boolean assignRiderToOrder(String orderId, String riderId, String riderName) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            
            
            if (order.getRiderId() != null && !order.getRiderId().isEmpty()) {
                logger.warn("Ordine {} già assegnato a {}", orderId, order.getRiderId());
                return false; 
            }

            order.setRiderId(riderId);
            order.setRiderName(riderName); 
            order.setOrderStatus("DELIVER"); 
            
            orderRepository.save(order);
            
            String orderJson = objectMapper.writeValueAsString(toDto(order));
            String topic = "shop/" + order.getShopId() + "/" + order.getId();
        
            logger.info("Invio aggiornamento stato MQTT su: {}", topic);
            mqttGateway.sendToMqtt(orderJson, topic);
           
            
            return true;
        }
        return false;
    }


   
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

        order.setRiderId(dto.getRiderId());
        order.setRiderName(dto.getRiderName());

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

        dto.setRiderId(entity.getRiderId());
        dto.setRiderName(entity.getRiderName());
        
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