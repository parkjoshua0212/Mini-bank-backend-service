package com.joshy.banking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "DEPOSIT", "WITHDRAWAL", "TRANSFER"

    private Double amount;

    private String sourceAccount;

    private String destinationAccount;

    private LocalDateTime timestamp;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(String type, Double amount, String sourceAccount, String destinationAccount) {
        this.type = type;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.timestamp = LocalDateTime.now();
    } 

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}

