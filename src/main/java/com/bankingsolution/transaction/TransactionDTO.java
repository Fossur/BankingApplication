package com.bankingsolution.transaction;

import com.bankingsolution.balance.Balance;
import com.bankingsolution.balance.BalanceDTO;
import com.bankingsolution.balance.BalanceService;
import com.bankingsolution.balance.Currency;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class TransactionDTO {

    private final Long accountId;
    private final Long transactionId;
    private final BigDecimal amount;
    private final Currency currency;
    private final Direction direction;
    private final String description;
    private final BalanceDTO balance;

    TransactionDTO(Transaction transaction, Balance balance) {
        this.accountId = transaction.getAccountId();
        this.transactionId = transaction.getId();
        this.amount = transaction.getAmount();
        this.currency = transaction.getCurrency();
        this.direction = transaction.getDirection();
        this.description = transaction.getDescription();
        this.balance = BalanceService.convert(balance);
    }

}
