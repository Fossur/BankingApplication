package com.bankingsolution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bankingsolution.account.AccountDTO;
import com.bankingsolution.account.AccountRequest;
import com.bankingsolution.account.AccountService;
import com.bankingsolution.balance.Currency;
import com.bankingsolution.transaction.Direction;
import com.bankingsolution.transaction.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {

    private static final String TRANSACTION_PATH = "/transaction";

    private static final String ACCOUNT_PATH = "/account";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    private AccountDTO createdAccount;

    @BeforeEach
    void setUp() {
        createdAccount = accountService.createAccount(generateAccountRequest());
    }

    @Test
    void whenMakeValidTransaction_returnCreated() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.balance.amount").value(transactionRequest.getAmount().doubleValue()));
    }

    @Test
    void whenMakeTransactionWithInvalidAccount_returnNotFound() throws Exception {

        var transactionRequest = generateTransactionRequest(Long.MAX_VALUE);

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenMakeTransactionWithNonBoundCurrency_returnNotFound() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());
        transactionRequest.setCurrency(Currency.SEK);

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenMakeOutTransactionWithInsufficientFunds_returnBadRequest() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());
        transactionRequest.setDirection(Direction.OUT);
        transactionRequest.setAmount(BigDecimal.valueOf(100));

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenMakeTransactionWithNegativeFunds_returnBadRequest() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());
        transactionRequest.setAmount(BigDecimal.valueOf(-100));

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenMakeTransactionWithNoDescription_returnBadRequest() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());
        transactionRequest.setDirection(null);

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenMakeTransactionWithBadCurrency_returnBadRequest() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());
        transactionRequest.setCurrency(null);

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetTransactionsByAccountOK_returnOK() throws Exception {

        var transactionRequest = generateTransactionRequest(createdAccount.getAccountId());

        mockMvc.perform(post(TRANSACTION_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(TRANSACTION_PATH + "/" + createdAccount.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(createdAccount.getAccountId()))
                .andExpect(jsonPath("$[0].balance.amount").value(transactionRequest.getAmount().doubleValue()));
    }


    @Test
    void whenCreateAccountValidRequest_returnCreated() throws Exception {

        mockMvc.perform(post(ACCOUNT_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(generateAccountRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists());
    }

    @Test
    void whenFindExistingAccount_returnOK() throws Exception {

        var createdAccount = accountService.createAccount(generateAccountRequest());

        mockMvc.perform(get(ACCOUNT_PATH + "/" + createdAccount.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").exists());
    }

    @Test
    void whenFindNonExistingAccount_returnNotFound() throws Exception {

        mockMvc.perform(get(ACCOUNT_PATH + "/" + Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    private static AccountRequest generateAccountRequest(List<Currency> currencies) {
        return new AccountRequest()
            .setCustomerId(1L)
            .setCountry("EE")
            .setCurrencies(currencies);
    }

    private static AccountRequest generateAccountRequest() {
        return generateAccountRequest(List.of(Currency.EUR, Currency.GBP));
    }

    private static TransactionRequest generateTransactionRequest(Long accountId) {
        return new TransactionRequest()
            .setAccountId(accountId)
            .setAmount(BigDecimal.TEN)
            .setCurrency(Currency.EUR)
            .setDescription("Description")
            .setDirection(Direction.IN);
    }
    
}
