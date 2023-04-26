package com.bankingsolution.account;

import com.bankingsolution.balance.BalanceDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class AccountDTO {

    private final Long accountId;
    private final Long customerId;
    private final String country;
    private final List<BalanceDTO> balances;

    AccountDTO(Account account, List<BalanceDTO> balances) {
        this.accountId = account.getId();
        this.customerId = account.getCustomerId();
        this.country = account.getCountry();
        this.balances = balances;
    }

}
