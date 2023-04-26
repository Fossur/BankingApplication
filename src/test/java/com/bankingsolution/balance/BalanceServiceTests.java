package com.bankingsolution.balance;

import com.bankingsolution.rabbitmq.PublishingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BalanceServiceTests {

    @MockBean
    private BalanceRepository balanceRepository;

    @MockBean
    private PublishingService publishingService;

    @Autowired
    private BalanceService balanceService;

    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = new Balance()
            .setId(1L)
            .setAmount(BigDecimal.TEN)
            .setAccountId(1L)
            .setCreatedOn(Instant.now())
            .setValidFrom(Instant.now())
            .setCurrency(Currency.EUR);
    }

    @Test
    void shouldCreateBalance() {

        balanceService.createAndPublishBalance(balance);

        verify(balanceRepository, times(1)).insert(any());
        verify(publishingService, times(1)).publishInsert((BalanceDTO) any());
    }

    @Test
    void shouldFindBalanceById() {

        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));

        var foundBalance = balanceService.findBalanceById(balance.getId());

        assertTrue(foundBalance.isPresent());
        assertEquals(balance.getId(), foundBalance.get().getId());
    }

    @Test
    void shouldInvalidateBalance() {

        balanceService.invalidateBalance(balance);

        verify(balanceRepository, times(1)).invalidateBalance(any());
        verify(publishingService, times(1)).publishUpdate(any());
    }

    @Test
    void shouldConvertBalanceToDTO() {

        var dto = BalanceService.convert(balance);

        assertEquals(balance.getAmount(), dto.getAmount());
        assertEquals(balance.getCurrency(), dto.getCurrency());
    }


}
