package com.joshy.banking.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.joshy.banking.dto.CreateAccountRequest;
import com.joshy.banking.dto.DepositRequest;
import com.joshy.banking.dto.WithdrawRequest;
import com.joshy.banking.entity.BankAccount;
import com.joshy.banking.entity.User;
import com.joshy.banking.repository.BankAccountRepository;
import com.joshy.banking.repository.UserRepository;
import com.joshy.banking.dto.TransferRequest;
import com.joshy.banking.entity.Transaction;
import com.joshy.banking.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public BankAccount createBankAccount(CreateAccountRequest request) {

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        String accountNumber = generateAccountNumber();

        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setUser(user);
        account.setBalance(0.0);

        return bankAccountRepository.save(account);
    }

    public BankAccount deposit(DepositRequest request) {

        BankAccount account = bankAccountRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow(() -> new RuntimeException("Account not found"));

        if (request.getAmount() <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        account.setBalance(account.getBalance() + request.getAmount());
        Transaction transaction = new Transaction(
            "DEPOSIT",
            request.getAmount(),
            null,
            account.getAccountNumber()
        );

        transactionRepository.save(transaction);
        return bankAccountRepository.save(account);

        
    }


    public BankAccount withdraw(WithdrawRequest request) {

        BankAccount account = bankAccountRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow(() -> new RuntimeException("Account not found"));

        if (request.getAmount() <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        if (account.getBalance() < request.getAmount()) {   
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - request.getAmount());

        Transaction transaction = new Transaction(
            "WITHDRAWAL",
            request.getAmount(),
            account.getAccountNumber(),
            null
        );

        transactionRepository.save(transaction);

        return bankAccountRepository.save(account);
    }


    @Transactional
    public void transfer(TransferRequest request) {

        if (request.getAmount() <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        BankAccount fromAccount = bankAccountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        BankAccount toAccount = bankAccountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient funds in sender account");
        }

        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction(
            "TRANSFER",
            request.getAmount(),
            fromAccount.getAccountNumber(),
            toAccount.getAccountNumber()
        );

        transactionRepository.save(transaction);


    }

    private String generateAccountNumber() {
        Random random = new Random();
        return "ACC" + (100000 + random.nextInt(900000));
    }
    
}
