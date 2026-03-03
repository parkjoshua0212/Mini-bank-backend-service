package com.joshy.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private String type; // "DEPOSIT", "WITHDRAWAL", "TRANSFER"
    private BigDecimal amount;
    private String from;
    private String to;
    private LocalDateTime timestamp;

    public TransactionDTO(String type, BigDecimal amount, String from, String to, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }

    public String getType() {return type;}
    public BigDecimal getAmount() {return amount;}
    public String getFrom() {return from;}
    public String getTo() {return to;}
    public LocalDateTime getTimestamp() {return timestamp;}
}