package com.bankingsolution.account;

import com.bankingsolution.balance.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class AccountRequest {

    @NotNull(message = "Customer id is missing!")
    private Long customerId;

    @NotBlank(message = "Country cannot be blank!")
    private String country;

    @NotEmpty(message = "No valid currencies found!")
    private List<Currency> currencies;

}
