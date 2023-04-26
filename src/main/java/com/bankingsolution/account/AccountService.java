package com.bankingsolution.account;

import com.bankingsolution.balance.BalanceService;
import com.bankingsolution.rabbitmq.PublishingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final BalanceService balanceService;

    private final PublishingService publishingService;

    @Transactional
    public AccountDTO createAccount(AccountRequest accountRequest) {

        var account = new Account(accountRequest);

        accountRepository.insert(account);

        var balances = accountRequest.getCurrencies().stream()
                .map(currency -> balanceService.createAndPublishBalance(account, currency))
                .map(BalanceService::convert)
                .collect(Collectors.toList());

        var dto = new AccountDTO(account, balances);

        publishingService.publishInsert(dto);

        return dto;
    }

    @Transactional(readOnly = true)
    public AccountDTO findExistingAccount(Long accountId) {

        var account = accountRepository.findById(accountId);

        if (account.isEmpty()) {
            throw new IllegalStateException(String.format("No account found with id %s", accountId));
        }

        var balances = balanceService.findValidBalancesByAccountId(accountId);

        return new AccountDTO(account.get(), balances);
    }

}
