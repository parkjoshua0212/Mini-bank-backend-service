package com.joshy.banking.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.joshy.banking.dto.CreateAccountRequest;
import com.joshy.banking.entity.BankAccount;
import com.joshy.banking.entity.User;
import com.joshy.banking.repository.BankAccountRepository;
import com.joshy.banking.repository.UserRepository;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
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


    private String generateAccountNumber() {
        Random random = new Random();
        return "ACC" + (100000 + random.nextInt(900000));
    }
    
}
