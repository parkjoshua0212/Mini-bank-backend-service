package com.joshy.banking.service;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.joshy.banking.dto.AccountResponse;
import com.joshy.banking.dto.CreateAccountRequest;
import com.joshy.banking.dto.DepositRequest;
import com.joshy.banking.dto.TransferRequest;
import com.joshy.banking.dto.WithdrawRequest;
import com.joshy.banking.entity.BankAccount;
import com.joshy.banking.entity.Transaction;
import com.joshy.banking.entity.User;
import com.joshy.banking.exception.BadRequestException;
import com.joshy.banking.repository.BankAccountRepository;
import com.joshy.banking.repository.TransactionRepository;
import com.joshy.banking.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public AccountResponse createBankAccount(CreateAccountRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String accountNumber = generateAccountNumber();

        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);

        BankAccount savedAccount = bankAccountRepository.save(account);

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    public AccountResponse deposit(DepositRequest request) {

        BankAccount account = bankAccountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new BadRequestException("Account not found"));

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be positive");
        }

        account.setBalance(
                account.getBalance().add(request.getAmount())
        );

        Transaction transaction = new Transaction(
                "DEPOSIT",
                request.getAmount(),
                null,
                account.getAccountNumber()
        );

        transactionRepository.save(transaction);

        BankAccount savedAccount = bankAccountRepository.save(account);

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    public AccountResponse withdraw(WithdrawRequest request) {

        BankAccount account = bankAccountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new BadRequestException("Account not found"));

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Withdrawal amount must be positive");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        account.setBalance(
                account.getBalance().subtract(request.getAmount())
        );

        Transaction transaction = new Transaction(
                "WITHDRAWAL",
                request.getAmount(),
                account.getAccountNumber(),
                null
        );

        transactionRepository.save(transaction);

        BankAccount savedAccount = bankAccountRepository.save(account);

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    @Transactional
    public void transfer(TransferRequest request) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be greater than zero");
        }

        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new BadRequestException("Cannot transfer to the same account");
        }

        BankAccount fromAccount = bankAccountRepository
                .findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new BadRequestException("Sender account not found"));

        BankAccount toAccount = bankAccountRepository
                .findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new BadRequestException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient funds in sender account");
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(request.getAmount())
        );

        toAccount.setBalance(
                toAccount.getBalance().add(request.getAmount())
        );

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
