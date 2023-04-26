package com.bankingsolution.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RabbitConfig {

    private final ExchangeProperties exchangeProperties;

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchangeProperties.getExchange());
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(exchangeProperties.getQueue())
                .deadLetterExchange(exchangeProperties.getDeadLetter().getExchange())
                .deadLetterRoutingKey(exchangeProperties.getDeadLetter().getRoutingKey())
                .build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(exchangeProperties.getRoutingKey());
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(exchangeProperties.getDeadLetter().getExchange());
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(exchangeProperties.getDeadLetter().getQueue()).build();
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(exchangeProperties.getDeadLetter().getRoutingKey());
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
