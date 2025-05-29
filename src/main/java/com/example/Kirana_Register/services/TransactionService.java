package com.example.Kirana_Register.services;

import com.example.Kirana_Register.Exceptions.DatabaseValidationException;
import com.example.Kirana_Register.Exceptions.ResourceNotFoundException;
import com.example.Kirana_Register.dto.CurrencyDTO;
import com.example.Kirana_Register.dto.ExchangeApiDTO;
import com.example.Kirana_Register.dto.TransactionDTO;
import com.example.Kirana_Register.entities.Currency;
import com.example.Kirana_Register.entities.Transaction;
import com.example.Kirana_Register.entities.Users;
import com.example.Kirana_Register.repositories.TransactionRepository;
import com.example.Kirana_Register.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrencyConversionService currencyConversionService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, CurrencyConversionService currencyConversionService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.currencyConversionService = currencyConversionService;
    }

    // Cache eviction happens when a new transaction is created
    @CacheEvict(value = "userTransactions", key = "#userId")
    public TransactionDTO createTransaction(TransactionDTO transactionDTO, Long userId) {
        if (transactionDTO == null) {
            log.warn("No User Id provided");
            throw new IllegalArgumentException("Transaction request cannot be null");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setUser(user);
        transaction.setType(transactionDTO.getType());
        transaction.setCurrency(transactionDTO.getCurrency());
        transaction.setTransactionDate(LocalDateTime.now());

        CurrencyDTO currencyDTO = convert(transactionDTO.getAmount(), transactionDTO.getCurrency());
        transaction.setAmountInr(currencyDTO.getAmountInr());
        transaction.setAmountUsd(currencyDTO.getAmountUsd());

        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            return mapToDTO(savedTransaction);
        } catch (DataIntegrityViolationException e) {
            log.warn("Invalid transaction data provided");
            throw new DatabaseValidationException("Invalid transaction data: constraint violation");
        }
    }

    // Cache the user transactions list
    @Cacheable(value = "userTransactions", key = "#userId")
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CurrencyDTO convert(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) {
            log.warn("Both amount and currency required");
            throw new IllegalArgumentException("Amount and currency cannot be null");
        }
        ExchangeApiDTO response = currencyConversionService.getExchangeRates();
        Double factor = response.getInrRate();
        if (factor == null) {
            log.warn("INR Conversion rate required");
            throw new ResourceNotFoundException("INR exchange rate not available");
        }
        BigDecimal amountUsd = null, amountInr = null;
        if ("USD".equalsIgnoreCase(currency.name())) {
            amountUsd = amount;
            amountInr = amount.multiply(BigDecimal.valueOf(factor));
        } else if ("INR".equalsIgnoreCase(currency.name())) {
            amountInr = amount;
            amountUsd = amount.divide(BigDecimal.valueOf(factor), 2, RoundingMode.HALF_UP);
        } else {
            log.warn("Currency other than INR or USD provided");
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        return new CurrencyDTO(amountUsd, amountInr);
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setUserId(transaction.getUser().getId());
        dto.setType(transaction.getType());
        dto.setCurrency(transaction.getCurrency());
        dto.setAmountInr(transaction.getAmountInr());
        dto.setAmountUsd(transaction.getAmountUsd());
        return dto;
    }
}