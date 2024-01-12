package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.mapper.AccountRequestMapper;
import br.com.teste.accountmanagement.mapper.AccountResponseMapper;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.repository.AccountRepository;
import br.com.teste.accountmanagement.service.AccountService;
import br.com.teste.accountmanagement.service.CustomerService;
import br.com.teste.accountmanagement.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PageableMapper pageableMapper;

    @Autowired
    private AccountResponseMapper accountResponseMapper;

    @Autowired
    private AccountRequestMapper accountRequestMapper;

    @Autowired
    private CustomerService customerService;

    @Override
    public PageResponseDTO getAccounts(Long customerId, Integer page, Integer size, String sort) {
        Customer customer = customerService.getById(customerId);

        Sort sortProperties = PaginationUtils.getSort(sort, Sort.Direction.DESC, "createdAt");

        PageRequest pageRequest = PageRequest.of(page - 1, size, sortProperties);
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        Page<Account> accountPage = accountRepository.findAllByCustomer(pageRequest, customer);

        if (accountPage != null) {
            pageResponseDTO.set_pageable(pageableMapper.toDto(accountPage));
            pageResponseDTO.set_content(accountResponseMapper.toDto(accountPage.getContent()));
        }

        return pageResponseDTO;
    }

    @Override
    public Account create(CreateAccountRequestDTO accountRequest) {
        Account account = accountRequestMapper.toEntity(accountRequest);
        return accountRepository.save(account);
    }

    @Override
    public Account getById(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Conta numero " + id + " não encontrada.");
        }

        return accountOptional.get();
    }

    @Override
    public void updateBalance(Long origin, Long destination, BigDecimal amount) {
        Account originAccount = getById(origin);
        Account destinationAccount = getById(destination);

        if (amount.compareTo(originAccount.getBalance()) > 0) {
            throw new RuntimeException("Conta não possui saldo suficiente para o pagamento");
        }

        originAccount.setBalance(originAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.saveAll(Arrays.asList(originAccount, destinationAccount));
    }


}
