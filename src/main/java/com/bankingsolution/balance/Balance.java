package com.bankingsolution.balance;

import com.bankingsolution.transaction.TransactionRequest;
import com.bankingsolution.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Balance {

    private Long id;
    private BigDecimal amount;
    private Long accountId;
    private Currency currency;
    private Instant createdOn = Instant.now();
    private Instant validFrom = Instant.now();
    private Instant validTo;

    Balance(Account account, Currency currency) {
        this.accountId = account.getId();
        this.currency = currency;
        this.amount = BigDecimal.ZERO;
    }

    Balance(TransactionRequest transaction, BigDecimal amount) {
        this.amount = amount;
        this.accountId = transaction.getAccountId();
        this.currency = transaction.getCurrency();
    }

}
