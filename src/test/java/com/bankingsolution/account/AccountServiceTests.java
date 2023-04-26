package com.bankingsolution.account;

import com.bankingsolution.balance.Balance;
import com.bankingsolution.balance.BalanceService;
import com.bankingsolution.balance.Currency;
import com.bankingsolution.rabbitmq.PublishingService;
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
class AccountServiceTests {
    
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private PublishingService publishingService;

    @Autowired
    private AccountService accountService;

    @Test
    void shouldCreateAnAccount() {

        var accountRequest = new AccountRequest()
                .setCountry("EE")
                .setCurrencies(List.of(Currency.EUR))
                .setCustomerId(1L);

        when(balanceService.createAndPublishBalance((Account) any(), any()))
            .thenReturn(
                new Balance()
                    .setValidFrom(Instant.now())
                    .setAmount(BigDecimal.ZERO)
                    .setCurrency(Currency.EUR)
                    .setCreatedOn(Instant.now())
                    .setAccountId(1L));

        var dto = accountService.createAccount(accountRequest);

        assertNotNull(dto);

        verify(publishingService, times(1)).publishInsert((AccountDTO) any());
    }

    @Test
    void shouldFindExistingAccount() {

        var accountId = 1L;

        when(accountRepository.findById(accountId))
            .thenReturn(
                Optional.of(new Account()
                    .setCountry("EE")
                    .setCreatedOn(Instant.now())
                    .setCustomerId(1L)
                    .setId(1L)));

        when(balanceService.findValidBalancesByAccountId(any()))
                .thenReturn(List.of());

        var dto = accountService.findExistingAccount(accountId);

        assertNotNull(dto);
        assertEquals(accountId, dto.getAccountId());
    }

    @Test
    void shouldNotFindNonExistingAccount() {

        var accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> accountService.findExistingAccount(accountId));
    }

}
