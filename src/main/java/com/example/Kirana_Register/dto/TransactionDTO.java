package com.example.Kirana_Register.dto;

import com.example.Kirana_Register.entities.Currency;
import com.example.Kirana_Register.entities.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal amountUsd;

    private BigDecimal amountInr;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Currency type is required")
    private Currency currency;

    private LocalDateTime transactionDate;

    private Long userId;
}
