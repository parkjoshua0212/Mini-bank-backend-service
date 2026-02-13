package com.joshy.banking.controller;

import com.joshy.banking.dto.CreateAccountRequest;
import com.joshy.banking.entity.BankAccount;
import com.joshy.banking.service.BankAccountService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public BankAccount createBankAccount(@RequestBody CreateAccountRequest request) {
        return bankAccountService.createBankAccount(request);
    }
    
}
