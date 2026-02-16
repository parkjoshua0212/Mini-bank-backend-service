package com.joshy.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {
    
    private String accountNumber;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountResponse(String accountNumber,
                            BigDecimal balance,
                            LocalDateTime createdAt) {
                                this.accountNumber = accountNumber;
                                this.balance = balance;
                                this.createdAt = createdAt;
                            }
                            

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
