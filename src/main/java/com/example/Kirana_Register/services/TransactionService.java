package com.example.Kirana_Register.services;

import com.example.Kirana_Register.dto.CurrencyDTO;
import com.example.Kirana_Register.dto.ExchangeApiDTO;
import com.example.Kirana_Register.dto.TransactionDTO;
import com.example.Kirana_Register.entities.Currency;
import com.example.Kirana_Register.entities.Transaction;
import com.example.Kirana_Register.entities.Users;
import com.example.Kirana_Register.repositories.TransactionRepository;
import com.example.Kirana_Register.repositories.UserRepository;
import com.example.Kirana_Register.security.CustomUserDetails;
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
        // Get the authenticated user
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setUser(user);
        transaction.setType(transactionDTO.getType());
        transaction.setCurrency(transactionDTO.getCurrency());
        transaction.setTransactionDate(LocalDateTime.now());

        CurrencyDTO currencyDTO = convert(transactionDTO.getAmount(), transactionDTO.getCurrency());

        transaction.setAmountInr(currencyDTO.getAmountInr());
        transaction.setAmountUsd(currencyDTO.getAmountUsd());


        Transaction savedTransaction = transactionRepository.save(transaction);

        TransactionDTO responseDTO = new TransactionDTO();
        responseDTO.setId(savedTransaction.getId());
        responseDTO.setAmount(savedTransaction.getAmount());
        responseDTO.setTransactionDate(savedTransaction.getTransactionDate());
        responseDTO.setUserId(savedTransaction.getUser().getId());
        responseDTO.setType(savedTransaction.getType());
        responseDTO.setCurrency(savedTransaction.getCurrency());
        responseDTO.setAmountInr(currencyDTO.getAmountInr());
        responseDTO.setAmountUsd(currencyDTO.getAmountUsd());

        return responseDTO;
    }

    public List<TransactionDTO> getUserTransactions() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();

        return transactionRepository.findByUserId(userId).stream().map(transaction -> {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(transaction.getId());
            dto.setAmount(transaction.getAmount());
            dto.setTransactionDate(transaction.getTransactionDate());
            dto.setUserId(userId);
            return dto;
        }).toList();
    }

    public CurrencyDTO convert(BigDecimal amount, Currency currency) {
        ExchangeApiDTO response = currencyConversionService.getExchangeRates();
        Double factor = response.getInrRate();
        BigDecimal amountUsd = null, amountInr = null;
        if ("USD".equalsIgnoreCase(currency.name())) {
            amountUsd = amount;
            amountInr = amount.multiply(BigDecimal.valueOf(factor));
        } else if ("INR".equalsIgnoreCase(currency.name())) {
            amountInr = amount;
            amountUsd = amount.divide(BigDecimal.valueOf(factor), 2, RoundingMode.HALF_UP);
        }
        CurrencyDTO currencyDTO = new CurrencyDTO(amountUsd, amountInr);
        return currencyDTO;
    }
}