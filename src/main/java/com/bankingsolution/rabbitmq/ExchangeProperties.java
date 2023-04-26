package com.bankingsolution.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rabbit")
class ExchangeProperties {

    private String exchange;
    private String queue;
    private String routingKey;
    private DeadLetter deadLetter;

    @Data
    static class DeadLetter {
        private String exchange;
        private String queue;
        private String routingKey;
    }

}
