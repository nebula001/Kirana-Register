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
import com.example.Kirana_Register.security.CustomUserDetails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrencyConversionService currencyConversionService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, CurrencyConversionService currencyConversionService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.currencyConversionService = currencyConversionService;
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction request cannot be null");
        }
        Long userId = extractUserId();
        return createTransactionHelper(transactionDTO, userId);
    }

    public List<TransactionDTO> getUserTransactions() {
        Long userId = extractUserId();
        return getUserTransactionsHelper(userId);
    }


    @CacheEvict(value = "transactions", key = "#userId")
    public TransactionDTO createTransactionHelper(TransactionDTO transactionDTO, Long userId) {
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
            throw new DatabaseValidationException("Invalid transaction data: constraint violation");
        }
    }

    @Cacheable(value = "transactions", key = "#userId")
    public List<TransactionDTO> getUserTransactionsHelper(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    private Long extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new AuthenticationServiceException("User not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }

    public CurrencyDTO convert(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("Amount and currency cannot be null");
        }
        ExchangeApiDTO response = currencyConversionService.getExchangeRates();
        Double factor = response.getInrRate();
        if (factor == null) {
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
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        CurrencyDTO currencyDTO = new CurrencyDTO(amountUsd, amountInr);
        return currencyDTO;
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