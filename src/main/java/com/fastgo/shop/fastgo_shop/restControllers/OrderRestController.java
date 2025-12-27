package com.fastgo.shop.fastgo_shop.restControllers;

import com.fastgo.shop.fastgo_shop.dto.OrderDto;
import com.fastgo.shop.fastgo_shop.service.OrderService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderRestController {

   
    private static final Logger logger = LoggerFactory.getLogger(OrderRestController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        // Log ingresso
        logger.info(">>> RICEVUTA RICHIESTA CREAZIONE ORDINE");
        logger.info("Cliente: {} | ShopID: {}", orderDto.getUsernameClient(), orderDto.getShopId());

        try {
            
            OrderDto createdOrder = orderService.processAndCreateOrder(orderDto);

            logger.info("<<< ORDINE COMPLETATO CON SUCCESSO. ID: {}", createdOrder.getId());
            return ResponseEntity.ok(createdOrder);

        } catch (Exception e) {
            logger.error("!!! ERRORE CRITICO NEL CONTROLLER", e);
            return ResponseEntity.internalServerError().body("Errore creazione ordine: " + e.getMessage());
        }
    }


    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getMyOrders(
            @RequestHeader("Authorization") String bearerToken) {
                try {
            String token = bearerToken.replace("Bearer ", "");
            
            if (!orderService.isTokenValid(token)) {
                return ResponseEntity.status(401).build();
            }

            
            String currentUserId = orderService.userId(token);
            
            logger.info("Richiesta ordini per utente estratto da Token: {}", currentUserId);

   
            List<OrderDto> orders = orderService.getOrdersByClientId(currentUserId);
            
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            logger.error("Errore recupero ordini utente", e);
            return ResponseEntity.internalServerError().build();
        }
}

@PatchMapping("/status")
    public ResponseEntity<?> updateOrderStatus(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody OrderDto statusUpdateDto) { 
        
        try {
            String token = bearerToken.replace("Bearer ", "");
            
    
            if (!orderService.isTokenValidShop(token)) {
                return ResponseEntity.status(401).build();
            }

            if (statusUpdateDto.getId() == null || statusUpdateDto.getOrderStatus() == null) {
                return ResponseEntity.badRequest().body("ID ordine e nuovo stato sono obbligatori");
            }

            OrderDto updatedOrder = orderService.updateOrderStatus(
                statusUpdateDto.getId(), 
                statusUpdateDto.getOrderStatus()
            );

            return ResponseEntity.ok(updatedOrder);

        } catch (Exception e) {
            logger.error("Errore aggiornamento stato ordine", e);
            return ResponseEntity.internalServerError().body("Errore aggiornamento: " + e.getMessage());
        }
    }
}