package com.joshy.banking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshy.banking.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountOrDestinationAccount(String sourceAccount, String destinationAccount);
    
    
}
