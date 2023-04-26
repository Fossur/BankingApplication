package com.bankingsolution.account;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Account {

    private Long id;
    private Long customerId;
    private String country;
    private Instant createdOn = Instant.now();

    Account(AccountRequest request) {
        this.customerId = request.getCustomerId();
        this.country = request.getCountry();
    }

}
