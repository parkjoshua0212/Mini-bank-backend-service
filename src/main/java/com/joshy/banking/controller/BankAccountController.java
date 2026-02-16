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

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Tag(name = "Bank Account API", description="Operations related to bank account management such as creating accounts, depositing, withdrawing, transferring funds, and viewing transaction history.")
@RestController
@RequestMapping("/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final TransactionRepository transactionRepository;

    public BankAccountController(BankAccountService bankAccountService, TransactionRepository transactionRepository) {
        this.bankAccountService = bankAccountService;
        this.transactionRepository = transactionRepository;
    }


    @Operation(summary = "Create a new bank account for a user",
                description="Creates a new bank account for an existing user. Generates a unique account number and initializes the balance to zero."
    )
    @ApiResponse(responseCode = "200", description = "Bank account created successfully")
    @PostMapping
    public AccountResponse createBankAccount(@RequestBody CreateAccountRequest request) {
        return bankAccountService.createBankAccount(request);
    }


    @Operation(summary="Deposit funds into a bank account",
                description="Deposits a specified amount into an existing bank account."
    )
    @ApiResponse(responseCode = "200", description = "Funds deposited successfully")
    @PostMapping("/deposit")
    public AccountResponse deposit(@Valid @RequestBody DepositRequest request) {
        return bankAccountService.deposit(request);
    }


    @Operation(summary="Withdraw funds from a bank account",
                description="Withdraws a specified amount from a bank account if ensured balance is available."
    )
    @ApiResponse(responseCode = "200", description = "Funds withdrawn successfully")
    @PostMapping("/withdraw")
    public AccountResponse withdraw(@RequestBody WithdrawRequest request) {
        return bankAccountService.withdraw(request);
    }


    @Operation(summary="Transfer funds between bank accounts",
                description="Transfers money from one account to another. Operation is transactional and will roll back if errors occur."
    )
    @ApiResponse(responseCode = "200", description = "Transfer completed successfully")
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
