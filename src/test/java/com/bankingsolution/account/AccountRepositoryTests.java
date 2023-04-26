package com.bankingsolution.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountRepositoryTests {

    @Autowired
    private AccountRepository accountRepository;

    private final Account account = new Account()
        .setCountry("EE")
        .setCreatedOn(Instant.now())
        .setCustomerId(1L);

    @BeforeEach
    void setUp() {
        accountRepository.insert(account);
    }

    @Test
    void shouldFindAccountById() {

        var foundAccount = accountRepository.findById(account.getId());
        assertTrue(foundAccount.isPresent());
    }

}
