package com.joshy.banking.service;

import java.math.BigDecimal;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.joshy.banking.dto.*;
import com.joshy.banking.entity.*;
import com.joshy.banking.exception.BadRequestException;
import com.joshy.banking.repository.*;


import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class BankAccountService {

    private static final Logger logger =
            LoggerFactory.getLogger(BankAccountService.class);

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

        logger.info("Creating bank account for user ID: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", request.getUserId());
                    return new RuntimeException("User not found");
                });

        String accountNumber = generateAccountNumber();

        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);

        BankAccount savedAccount = bankAccountRepository.save(account);

        logger.info("Bank account created successfully: {}", savedAccount.getAccountNumber());

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    public AccountResponse deposit(DepositRequest request) {

        logger.info("Deposit request: Account={}, Amount={}",
                request.getAccountNumber(), request.getAmount());

        BankAccount account = bankAccountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> {
                    logger.error("Deposit failed. Account not found: {}",
                            request.getAccountNumber());
                    return new RuntimeException("Account not found");
                });

        account.setBalance(account.getBalance().add(request.getAmount()));

        Transaction transaction = new Transaction(
                "DEPOSIT",
                request.getAmount(),
                null,
                account.getAccountNumber()
        );

        transactionRepository.save(transaction);
        BankAccount savedAccount = bankAccountRepository.save(account);

        logger.info("Deposit successful. New balance for {} is {}",
                savedAccount.getAccountNumber(),
                savedAccount.getBalance());

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    public AccountResponse withdraw(WithdrawRequest request) {

        logger.info("Withdrawal request: Account={}, Amount={}",
                request.getAccountNumber(), request.getAmount());

        BankAccount account = bankAccountRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> {
                    logger.error("Withdrawal failed. Account not found: {}",
                            request.getAccountNumber());
                    return new RuntimeException("Account not found");
                });

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn("Insufficient funds for account {}. Current balance: {}",
                    account.getAccountNumber(),
                    account.getBalance());
            throw new BadRequestException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));

        Transaction transaction = new Transaction(
                "WITHDRAWAL",
                request.getAmount(),
                account.getAccountNumber(),
                null
        );

        transactionRepository.save(transaction);
        BankAccount savedAccount = bankAccountRepository.save(account);

        logger.info("Withdrawal successful. New balance for {} is {}",
                savedAccount.getAccountNumber(),
                savedAccount.getBalance());

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    @Transactional
    public void transfer(TransferRequest request) {

        logger.info("Transfer request: From={}, To={}, Amount={}",
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount());

        BankAccount fromAccount = bankAccountRepository
                .findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> {
                    logger.error("Sender account not found: {}",
                            request.getFromAccount());
                    return new BadRequestException("Sender account not found");
                });

        BankAccount toAccount = bankAccountRepository
                .findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> {
                    logger.error("Receiver account not found: {}",
                            request.getToAccount());
                    return new BadRequestException("Receiver account not found");
                });

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn("Transfer failed. Insufficient funds in account {}",
                    fromAccount.getAccountNumber());
            throw new BadRequestException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction(
                "TRANSFER",
                request.getAmount(),
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber()
        );

        transactionRepository.save(transaction);

        logger.info("Transfer successful from {} to {}",
                request.getFromAccount(),
                request.getToAccount());
    }

    private String generateAccountNumber() {
        Random random = new Random();
        return "ACC" + (100000 + random.nextInt(900000));
    }

    public Page<TransactionDTO> getTransactions(String accountNumber, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

         Pageable pageable = PageRequest.of(page, size, sort);

         Page<Transaction> transactions = transactionRepository
                .findBySourceAccountOrDestinationAccount(
                        accountNumber,
                        accountNumber,
                        pageable
                );

        return transactions.map(transaction ->
                new TransactionDTO(
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getSourceAccount(),
                        transaction.getDestinationAccount(),
                        transaction.getTimestamp()
                )
        );

    }
}

