package com.bankingsolution.balance;

import com.bankingsolution.rabbitmq.PublishingService;
import com.bankingsolution.transaction.TransactionRequest;
import com.bankingsolution.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    private final PublishingService publishingService;

    public Optional<Balance> findBalanceById(Long balanceId) {
        return balanceRepository.findById(balanceId);
    }

    public Balance createAndPublishBalance(Account account, Currency currency) {
        return createAndPublishBalance(new Balance(account, currency));
    }

    public Balance createAndPublishBalance(TransactionRequest transaction, BigDecimal amount) {
        return createAndPublishBalance(new Balance(transaction, amount));
    }

    public Balance createAndPublishBalance(Balance balance) {
        balanceRepository.insert(balance);
        publishingService.publishInsert(convert(balance));
        return balance;
    }

    public void invalidateBalance(Balance balance) {
        balanceRepository.invalidateBalance(balance);
        publishingService.publishUpdate(convert(balance));
    }

    public Optional<Balance> findBalanceByAccountIdAndCurrency(Long accountId, Currency currency) {
        return balanceRepository.findValidBalanceByAccountIdAndCurrency(accountId, currency);
    }

    public List<BalanceDTO> findValidBalancesByAccountId(Long accountId) {
        var balances = balanceRepository.findValidByAccountId(accountId);

        return balances.stream()
                .map(BalanceService::convert)
                .collect(Collectors.toList());
    }

    public static BalanceDTO convert(Balance balance) {
        return new BalanceDTO(balance);
    }

}
