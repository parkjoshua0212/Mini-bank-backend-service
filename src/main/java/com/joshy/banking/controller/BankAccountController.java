package com.joshy.banking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshy.banking.dto.AccountResponse;
import com.joshy.banking.dto.CreateAccountRequest;
import com.joshy.banking.dto.DepositRequest;
import com.joshy.banking.dto.TransferRequest;
import com.joshy.banking.dto.WithdrawRequest;
import com.joshy.banking.entity.Transaction;
import com.joshy.banking.repository.TransactionRepository;
import com.joshy.banking.service.BankAccountService;



@RestController
@RequestMapping("/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final TransactionRepository transactionRepository;

    public BankAccountController(BankAccountService bankAccountService, TransactionRepository transactionRepository) {
        this.bankAccountService = bankAccountService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public AccountResponse createBankAccount(@RequestBody CreateAccountRequest request) {
        return bankAccountService.createBankAccount(request);
    }

    @PostMapping("/deposit")
    public AccountResponse deposit(@RequestBody DepositRequest request) {
        return bankAccountService.deposit(request);
    }

    @PostMapping("/withdraw")
    public AccountResponse withdraw(@RequestBody WithdrawRequest request) {
        return bankAccountService.withdraw(request);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request){
        bankAccountService.transfer(request);
        return "Transfer successful";
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<Transaction> getTransactions(@PathVariable String accountNumber) {
        return transactionRepository.findBySourceAccountOrDestinationAccount(accountNumber, accountNumber);
    }
    
}
