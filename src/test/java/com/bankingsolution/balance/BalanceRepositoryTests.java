package com.bankingsolution.balance;

import com.bankingsolution.account.Account;
import com.bankingsolution.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class BalanceRepositoryTests {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    private Balance balance;

    @BeforeEach
    void setUp() {

        account = new Account()
            .setCountry("EE")
            .setCreatedOn(Instant.now())
            .setCustomerId(1L);

        accountRepository.insert(account);

        balance = new Balance()
            .setValidFrom(Instant.now())
            .setAmount(BigDecimal.TEN)
            .setCreatedOn(Instant.now())
            .setCurrency(Currency.SEK)
            .setAccountId(account.getId());

        balanceRepository.insert(balance);
    }

    @Test
    void shouldCreateValidBalance() {

        var newBalance = new Balance()
            .setValidFrom(Instant.now())
            .setAmount(BigDecimal.TEN)
            .setCreatedOn(Instant.now())
            .setCurrency(Currency.SEK)
            .setAccountId(account.getId());

        balanceRepository.insert(newBalance);

        assertNotNull(newBalance.getId());
        assertNull(newBalance.getValidTo());
    }

    @Test
    void shouldFindBalanceById() {

        var foundBalance = balanceRepository.findById(balance.getId());
        assertTrue(foundBalance.isPresent());
    }

    @Test
    void shouldFindValidBalancesByAccountId() {

        var foundBalance = balanceRepository.findValidByAccountId(balance.getAccountId());
        assertEquals(1, foundBalance.size());
    }


    @Test
    void shouldFindValidBalancesByAccountIdAndCurrency() {

        var foundBalance = balanceRepository
                .findValidBalanceByAccountIdAndCurrency(balance.getAccountId(), balance.getCurrency());

        assertTrue(foundBalance.isPresent());
    }

    @Test
    void shouldInvalidateBalance() {

        balanceRepository.invalidateBalance(balance);

        var foundBalance = balanceRepository.findById(balance.getId());

        assertTrue(foundBalance.isPresent());
        assertNotNull(foundBalance.get().getValidTo());
    }

}
