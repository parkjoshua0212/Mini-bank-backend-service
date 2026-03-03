package com.joshy.banking.repository;

import com.joshy.banking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findBySourceAccountOrDestinationAccount(
            String sourceAccount,
            String destinationAccount,
            Pageable pageable
    );
}
