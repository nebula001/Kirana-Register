package com.example.Kirana_Register.repositories;

import com.example.Kirana_Register.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction> findByUser_IdAndTransactionDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
