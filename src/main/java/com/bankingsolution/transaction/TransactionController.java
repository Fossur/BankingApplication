package com.bankingsolution.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
class TransactionController {

    private final TransactionService transactionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> makeTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {

        var res = transactionService.makeTransaction(transactionRequest);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Object> getTransactionsByAccountId(@PathVariable Long accountId) {

        var res = transactionService.findTransactionsByAccountId(accountId);
        return ResponseEntity.ok(res);
    }

}
