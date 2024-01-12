package br.com.teste.accountmanagement.controller;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.PostResponseDTO;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.service.AccountService;
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
@RequestMapping("/api/v1/customers/{customerId}/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<PageResponseDTO> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "_sort", required = false) String sort,
            @PathVariable Long customerId) {
        return new ResponseEntity<PageResponseDTO>(accountService.getAccounts(customerId, page, size, sort), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> create(
            @RequestBody @Valid CreateAccountRequestDTO account,
            @PathVariable Long customerId) {
        Account createdAccount = accountService.create(account, customerId);

        URI locationResource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.getId())
                .toUri();
        log.info("Successfully created Account with ID: " + createdAccount.getId());
        return ResponseEntity.created(locationResource).body(PostResponseDTO.builder().id(createdAccount.getId()).build());
    }
}
