package com.bankingsolution.transaction;

import com.bankingsolution.account.AccountService;
import com.bankingsolution.balance.Balance;
import com.bankingsolution.balance.BalanceService;
import com.bankingsolution.rabbitmq.PublishingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final BalanceService balanceService;

    private final AccountService accountService;

    private final PublishingService publishingService;

    @Transactional
    public TransactionDTO makeTransaction(TransactionRequest transactionRequest) {

        var existingBalance = findExistingBalance(transactionRequest);

        balanceService.invalidateBalance(existingBalance);

        var newAmount = calculateNewAmount(existingBalance, transactionRequest);

        var newBalance = balanceService.createAndPublishBalance(transactionRequest, newAmount);

        return createAndPublishTransaction(transactionRequest, newBalance);
    }

    private TransactionDTO createAndPublishTransaction(TransactionRequest request, Balance balance) {

        var transaction = new Transaction(request, balance);
        transactionRepository.insert(transaction);

        var dto =  new TransactionDTO(transaction, balance);
        publishingService.publishInsert(dto);

        return dto;
    }

    Balance findExistingBalance(TransactionRequest transactionRequest) {

        var accountId = transactionRequest.getAccountId();

        accountService.findExistingAccount(accountId);

        var balance = balanceService
                .findBalanceByAccountIdAndCurrency(accountId, transactionRequest.getCurrency());

        if (balance.isEmpty()) {
            throw new IllegalStateException(String.format("No existing balance found for account %s and currency %s",
                    accountId, transactionRequest.getCurrency()));
        }

        return balance.get();
    }

    BigDecimal calculateNewAmount(Balance balance, TransactionRequest transactionRequest) {

        var oldAmount = balance.getAmount();
        var transactionAmount = transactionRequest.getAmount();

        var newAmount = switch (transactionRequest.getDirection()) {
            case IN -> oldAmount.add(transactionAmount);
            case OUT -> oldAmount.subtract(transactionAmount);
        };

        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A balance cannot be negative!");
        }

        return newAmount;
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> findTransactionsByAccountId(Long accountId) {

        var transactions = transactionRepository.findByAccountId(accountId);

        return transactions.stream().map(transaction -> {
                var balance = balanceService.findBalanceById(transaction.getBalanceAfter());

                if (balance.isEmpty()) {
                    throw new IllegalStateException(String.format("No balance found for id %s",
                            transaction.getBalanceAfter()));
                }

                return new TransactionDTO(transaction, balance.get());
            })
            .toList();
    }

}
