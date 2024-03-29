package br.com.teste.accountmanagement.controller;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Log4j2
@RestController
@RequestMapping("/api/v1/customers/{customerId}/accounts/{accountId}")
public class TransactionController {

    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<PageResponseDTO> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "_sort", required = false) String sort,
            @PathVariable Long customerId,
            @PathVariable Long accountId) {
        return new ResponseEntity<PageResponseDTO>(transactionService.getTransactions(
                customerId,
                accountId,
                page,
                size,
                sort),
                HttpStatus.OK);
    }

    @PostMapping("/transactions")
    public ResponseEntity<NewTransactionResponseDTO> create(
            @RequestBody @Valid CreateTransactionRequestDTO transaction,
            @PathVariable Long accountId,
            @PathVariable Long customerId) {
        NewTransactionResponseDTO createdTransaction = transactionService.create(transaction, accountId, customerId);

        URI locationResource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTransaction.getId())
                .toUri();
        log.info("Successfully created Transaction with ID: " + createdTransaction.getId());
        return ResponseEntity.created(locationResource).body(createdTransaction);
    }

    @PostMapping("/transactions/{transactionId}/cancel")
    public ResponseEntity<NewTransactionResponseDTO> cancel(
        @PathVariable Long accountId,
        @PathVariable Long transactionId,
        @PathVariable Long customerId) {
        NewTransactionResponseDTO createdTransaction = transactionService.cancel(transactionId, accountId, customerId);

        log.info("Successfully cancelled Transaction with ID: "
                + transactionId
                + " and created Transaction with ID: "
                + createdTransaction.getId());
        return new ResponseEntity<NewTransactionResponseDTO>(createdTransaction, HttpStatus.OK);
    }
}
