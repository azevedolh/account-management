package br.com.teste.accountmanagement.controller;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.PostResponseDTO;
import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.model.Transaction;
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

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<PageResponseDTO> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "_sort", required = false) String sort,
            @PathVariable Long accountId) {
        return new ResponseEntity<PageResponseDTO>(transactionService.getTransactions(accountId, page, size, sort), HttpStatus.OK);
    }

    @PostMapping("/transactions")
    public ResponseEntity<PostResponseDTO> create(
            @RequestBody @Valid CreateTransactionRequestDTO transaction,
            @PathVariable Long accountId) {
        Transaction createdTransaction = transactionService.create(transaction, accountId);

        URI locationResource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTransaction.getId())
                .toUri();
        log.info("Successfully created Transaction with ID: " + createdTransaction.getId());
        return ResponseEntity.created(locationResource).body(PostResponseDTO.builder().id(createdTransaction.getId()).build());
    }

    @PostMapping("/transactions:cancel")
    public ResponseEntity<TransactionResponseDTO> cancel(
            @RequestBody @Valid CancelTransactionRequestDTO transaction,
            @PathVariable Long accountId) {
        TransactionResponseDTO createdTransaction = transactionService.cancel(transaction, accountId);

        URI locationResource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTransaction.getId())
                .toUri();
        log.info("Successfully cancelled Transaction with ID: "
                + transaction.getId()
                + " and created Transaction with ID: "
                + createdTransaction.getId());
        return new ResponseEntity<TransactionResponseDTO>(createdTransaction, HttpStatus.OK);
    }
}
