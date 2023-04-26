package com.bankingsolution.transaction;

import com.bankingsolution.balance.Currency;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class TransactionRequest {

    @NotNull(message = "Account id is missing!")
    private Long accountId;

    @Positive(message = "Amount should be a positive number!")
    private BigDecimal amount;

    @NotNull(message = "Currency is missing!")
    private Currency currency;

    @NotNull(message = "Direction is missing!")
    private Direction direction;

    @NotBlank(message = "Description cannot be blank!")
    private String description;

}
