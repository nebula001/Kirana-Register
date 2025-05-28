package com.example.Kirana_Register.controllers;


import com.example.Kirana_Register.dto.TransactionDTO;
import com.example.Kirana_Register.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO responseDTO = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getUserTransactions() {
        return ResponseEntity.ok(transactionService.getUserTransactions());
    }
}
