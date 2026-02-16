package com.joshy.banking.dto;

import java.math.BigDecimal;

public class WithdrawRequest {
    
    private String accountNumber;
    private BigDecimal amount;

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
