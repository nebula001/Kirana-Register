package com.example.Kirana_Register.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal amountUsd;

    private BigDecimal amountInr;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Currency type is required")
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
