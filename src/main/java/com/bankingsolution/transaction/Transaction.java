package com.bankingsolution.transaction;

import com.bankingsolution.balance.Balance;
import com.bankingsolution.balance.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Transaction {

    private Long id;
    private BigDecimal amount;
    private String description;
    private Direction direction;
    private Long accountId;
    private Currency currency;
    private Long balanceAfter;
    private Instant createdOn = Instant.now();

    Transaction(TransactionRequest transaction, Balance balance) {
        this.accountId = transaction.getAccountId();
        this.amount = transaction.getAmount();
        this.currency = transaction.getCurrency();
        this.direction = transaction.getDirection();
        this.description = transaction.getDescription();
        this.balanceAfter = balance.getId();
    }

}
