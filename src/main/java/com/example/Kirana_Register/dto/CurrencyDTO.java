package com.example.Kirana_Register.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrencyDTO {
    private BigDecimal amountUsd;

    private BigDecimal amountInr;
}
