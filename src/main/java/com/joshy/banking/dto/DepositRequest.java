package com.joshy.banking.dto;

public class DepositRequest {
    
    private String accountNumber;
    private Double amount;

    public String getAccountNumber() {
        return accountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
