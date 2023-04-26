package com.bankingsolution.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
class AccountController {

    private final AccountService accountService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDTO> createAccount(@RequestBody @Valid AccountRequest accountRequest) {

        var res = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable @Positive Long accountId) {

        var res = accountService.findExistingAccount(accountId);
        return ResponseEntity.ok(res);
    }

}
