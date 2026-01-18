package com.fastgo.shop.fastgo_shop.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.sync:sync-exchange}")
    private String syncExchange;

    private final String ROUTING_KEY_SHOP_SYNC = "shop.sync.request";
    private final String QUEUE_NAME_SHOP_SYNC = "shop.sync.request.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(syncExchange);
    }

    @Bean
    public Queue shopSyncQueue() {
        return new Queue(QUEUE_NAME_SHOP_SYNC);
    }

    @Bean
    public Binding shopSyncBinding() {
        return BindingBuilder.bind(shopSyncQueue())
                .to(syncExchange())
                .with(ROUTING_KEY_SHOP_SYNC);
    }


    
    public static final String ORDER_EXCHANGE = "orders-exchange";
    

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }


    public static final String ORDER_QUEUE = "rider.order.queue";
    public static final String ROUTING_KEY_ORDER = "order.created";

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ROUTING_KEY_ORDER);
    }


    public static final String ACCEPT_QUEUE = "shop.order.accept.queue";
    public static final String ACCEPT_ROUTING_KEY = "order.accept";

    @Bean
    public Queue acceptQueue() {
        return QueueBuilder.durable(ACCEPT_QUEUE).build();
    }

    @Bean
    public Binding acceptBinding() {
        return BindingBuilder.bind(acceptQueue())
                .to(orderExchange()) 
                .with(ACCEPT_ROUTING_KEY);
    }


    public static final String UPDATE_STATUS_QUEUE = "shop.order.status.update.queue";
    public static final String UPDATE_STATUS_ROUTING_KEY = "order.status.update";

    @Bean
    public Queue updateStatusQueue() {
        return new Queue(UPDATE_STATUS_QUEUE);
    }


    @Bean
    public Binding bindingUpdateStatus() {
        return BindingBuilder.bind(updateStatusQueue())
                .to(orderExchange()) 
                .with(UPDATE_STATUS_ROUTING_KEY);
    }


}