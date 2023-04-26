package com.bankingsolution.balance;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class BalanceDTO {

    private final BigDecimal amount;
    private final Currency currency;

    BalanceDTO(Balance balance) {
        this.amount = balance.getAmount();
        this.currency = balance.getCurrency();
    }

}
