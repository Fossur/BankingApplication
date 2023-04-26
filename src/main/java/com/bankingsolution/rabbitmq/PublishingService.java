package com.bankingsolution.rabbitmq;

import com.bankingsolution.account.AccountDTO;
import com.bankingsolution.transaction.TransactionDTO;
import com.bankingsolution.balance.BalanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishingService {

    private final AmqpTemplate rabbitTemplate;

    private final ExchangeProperties exchangeProperties;

    public void publishInsert(AccountDTO account) {
        publishMessage(account);
    }

    public void publishInsert(BalanceDTO balance) {
        publishMessage(balance);
    }

    public void publishInsert(TransactionDTO transaction) {
        publishMessage(transaction);
    }

    public void publishUpdate(BalanceDTO balance) {
        publishMessage(balance);
    }

    private void publishMessage(Object object) {
        rabbitTemplate.convertAndSend(exchangeProperties.getExchange(), exchangeProperties.getRoutingKey(), object);
    }

}
