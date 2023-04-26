package com.bankingsolution.transaction;

import com.bankingsolution.account.AccountService;
import com.bankingsolution.balance.Balance;
import com.bankingsolution.balance.BalanceService;
import com.bankingsolution.balance.Currency;
import com.bankingsolution.rabbitmq.PublishingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionServiceTests {

    @MockBean
    private PublishingService publishingService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountService accountService;

    @MockBean
    private BalanceService balanceService;

    @Autowired
    private TransactionService transactionService;

    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = new Balance()
            .setCurrency(Currency.EUR)
            .setValidFrom(Instant.now())
            .setCreatedOn(Instant.now())
            .setAccountId(1L)
            .setAmount(BigDecimal.ONE);
    }

    @Test
    void shouldMakeTransaction() {

        var accountId = 1L;

        when(balanceService.findBalanceByAccountIdAndCurrency(accountId, Currency.EUR))
            .thenReturn(
                Optional.of(new Balance()
                    .setAccountId(accountId)
                    .setCreatedOn(Instant.now())
                    .setValidFrom(Instant.now())
                    .setCurrency(Currency.EUR)
                    .setAmount(BigDecimal.TEN)
                    .setId(1L)));

        when(balanceService.createAndPublishBalance((TransactionRequest) any(), any()))
            .thenReturn(
                new Balance()
                    .setId(2L)
                    .setAmount(BigDecimal.valueOf(20))
                    .setCurrency(Currency.EUR)
                    .setValidFrom(Instant.now())
                    .setCreatedOn(Instant.now())
                    .setAccountId(accountId));

        var transactionRequest = new TransactionRequest()
            .setAccountId(accountId)
            .setAmount(BigDecimal.TEN)
            .setCurrency(Currency.EUR)
            .setDescription("Description")
            .setDirection(Direction.IN);

        var dto = transactionService.makeTransaction(transactionRequest);

        assertNotNull(dto);
        assertNotNull(dto.getBalance());

        verify(publishingService, times(1)).publishInsert((TransactionDTO) any());

    }

    @Test
    void shouldCalculateCorrectInAmount() {

        var accountId = 1L;

        var transactionRequest = new TransactionRequest()
            .setAccountId(accountId)
            .setAmount(BigDecimal.TEN)
            .setCurrency(Currency.EUR)
            .setDescription("Description")
            .setDirection(Direction.IN);

        var res = transactionService.calculateNewAmount(balance, transactionRequest);

        assertEquals(11.0, res.doubleValue());
    }

    @Test
    void shouldCalculateCorrectOutAmount() {

        var accountId = 1L;

        var transactionRequest = new TransactionRequest()
            .setAccountId(accountId)
            .setAmount(BigDecimal.ONE)
            .setCurrency(Currency.EUR)
            .setDescription("Description")
            .setDirection(Direction.OUT);

        var res = transactionService.calculateNewAmount(balance, transactionRequest);

        assertEquals(0.0, res.doubleValue());
    }

    @Test
    void shouldThrowErrorOnNegativeAmount() {

        var accountId = 1L;

        var transactionRequest = new TransactionRequest()
            .setAccountId(accountId)
            .setAmount(BigDecimal.TEN)
            .setCurrency(Currency.EUR)
            .setDescription("Description")
            .setDirection(Direction.OUT);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.calculateNewAmount(balance, transactionRequest));
    }


    @Test
    void shouldFindTransactionsByAccountId() {

        var accountId = 1L;

        when(transactionRepository.findByAccountId(accountId))
                .thenReturn(
                    List.of(new Transaction()
                        .setId(1L)
                        .setDirection(Direction.IN)
                        .setAmount(BigDecimal.ONE)
                        .setCreatedOn(Instant.now())
                        .setAccountId(accountId)
                        .setCurrency(Currency.EUR)
                        .setBalanceAfter(balance.getId())));

        when(balanceService.findBalanceById(balance.getId()))
                .thenReturn(
                    Optional.of(new Balance()
                        .setId(2L)
                        .setAmount(BigDecimal.valueOf(20))
                        .setCurrency(Currency.EUR)
                        .setValidFrom(Instant.now())
                        .setCreatedOn(Instant.now())
                        .setAccountId(accountId)));

        var transactions = transactionService.findTransactionsByAccountId(accountId);

        assertEquals(1, transactions.size());
    }

}
