package com.bankingsolution.transaction;

import com.bankingsolution.account.Account;
import com.bankingsolution.account.AccountRepository;
import com.bankingsolution.balance.Balance;
import com.bankingsolution.balance.BalanceRepository;
import com.bankingsolution.balance.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransactionRepositoryTests {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account account;

    @BeforeEach
    void setUp() {

        account = new Account()
            .setCountry("EE")
            .setCreatedOn(Instant.now())
            .setCustomerId(1L);

        accountRepository.insert(account);

        var balance = new Balance()
            .setValidFrom(Instant.now())
            .setAmount(BigDecimal.TEN)
            .setCreatedOn(Instant.now())
            .setCurrency(Currency.SEK)
            .setAccountId(account.getId());

        balanceRepository.insert(balance);

        var transaction = new Transaction()
            .setDirection(Direction.IN)
            .setCreatedOn(Instant.now())
            .setAccountId(account.getId())
            .setAmount(BigDecimal.TEN)
            .setBalanceAfter(balance.getId())
            .setCurrency(balance.getCurrency())
            .setDescription("Description");

        transactionRepository.insert(transaction);

    }

    @Test
    void shouldFindTransactionByAccountId() {

        var foundTransactions = transactionRepository.findByAccountId(account.getId());
        assertEquals(1, foundTransactions.size());
    }

}
