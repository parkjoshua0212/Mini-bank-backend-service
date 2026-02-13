package com.joshy.banking.dto;

import java.time.LocalDateTime;

public class AccountResponse {
    
    private String accountNumber;
    private Double balance;
    private LocalDateTime createdAt;

    public AccountResponse(String accountNumber,
                            Double balance,
                            LocalDateTime createdAt) {
                                this.accountNumber = accountNumber;
                                this.balance = balance;
                                this.createdAt = createdAt;
                            }
                            

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
