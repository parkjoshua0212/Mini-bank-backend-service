package com.joshy.banking.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

public class TransferRequest {
    
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;


    @NotBlank(message = "From account number is required")
    public String getFromAccount() {
        return fromAccount;
    }

    @NotBlank(message = "To account number is required")
    public String getToAccount() {
        return toAccount;
    }

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    public BigDecimal getAmount() {
        return amount;
    }

    
}
