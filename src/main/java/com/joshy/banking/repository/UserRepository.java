package com.joshy.banking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshy.banking.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query method to find user by email
    Optional<User> findByEmail(String email);
}
