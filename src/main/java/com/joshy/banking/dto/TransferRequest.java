package com.joshy.banking.dto;

public class TransferRequest {
    
    private String fromAccount;
    private String toAccount;
    private Double amount;

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public Double getAmount() {
        return amount;
    }

    
}
