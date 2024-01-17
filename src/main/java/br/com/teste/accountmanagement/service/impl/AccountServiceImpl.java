package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.AccountResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.mapper.AccountRequestMapper;
import br.com.teste.accountmanagement.mapper.AccountResponseMapper;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.repository.AccountRepository;
import br.com.teste.accountmanagement.service.AccountService;
import br.com.teste.accountmanagement.service.CustomerService;
import br.com.teste.accountmanagement.util.MessageUtil;
import br.com.teste.accountmanagement.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static br.com.teste.accountmanagement.util.ConstantUtil.SORT_BY_CREATED_AT;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private PageableMapper pageableMapper;
    private AccountResponseMapper accountResponseMapper;
    private AccountRequestMapper accountRequestMapper;
    private CustomerService customerService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              PageableMapper pageableMapper,
                              AccountResponseMapper accountResponseMapper,
                              AccountRequestMapper accountRequestMapper,
                              CustomerService customerService) {
        this.accountRepository = accountRepository;
        this.pageableMapper = pageableMapper;
        this.accountResponseMapper = accountResponseMapper;
        this.accountRequestMapper = accountRequestMapper;
        this.customerService = customerService;
    }

    @Override
    public PageResponseDTO getAccounts(Long customerId, Integer page, Integer size, String sort) {
        Customer customer = customerService.getById(customerId);

        Sort sortProperties = PaginationUtil.getSort(sort, Sort.Direction.DESC, SORT_BY_CREATED_AT);

        PageRequest pageRequest = PageRequest.of(page - 1, size, sortProperties);
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        Page<Account> accountPage = accountRepository.findAllByCustomerAndIsActive(pageRequest, customer, Boolean.TRUE);

        if (accountPage != null) {
            pageResponseDTO.set_pageable(pageableMapper.toDto(accountPage));
            pageResponseDTO.set_content(accountResponseMapper.toDto(accountPage.getContent()));
        }

        return pageResponseDTO;
    }

    @Override
    public AccountResponseDTO create(CreateAccountRequestDTO accountRequest, Long customerId) {
        Customer customer = customerService.getById(customerId);
        Account account = accountRequestMapper.toEntity(accountRequest);
        account.setCustomer(customer);
        account = accountRepository.save(account);
        return accountResponseMapper.toDto(account);
    }

    @Override
    public Account getById(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (accountOptional.isEmpty()) {
            String message = MessageUtil.getMessage("account.not.found", id.toString());
            throw new CustomBusinessException(HttpStatus.NOT_FOUND, message);
        }

        return accountOptional.get();
    }

    @Override
    public void updateBalance(Long accountId, OperationEnum operation, BigDecimal amount) throws CustomBusinessException {

        Account account = getById(accountId);

        if (operation == null) {
            String message = MessageUtil.getMessage("account.operation.not.informed");
            throw new CustomBusinessException(message);
        }

        if (amount == null || amount.compareTo(new BigDecimal("0")) <= 0) {
            String message = MessageUtil.getMessage("account.amount.invalid");
            String details = MessageUtil.getMessage("account.amount.invalid.details");
            throw new CustomBusinessException(message, details);
        }

        if (OperationEnum.DEBITO == operation) {
            if (amount.compareTo(account.getBalance()) > 0) {
                String message = MessageUtil.getMessage("account.not.enough.balance");
                throw new CustomBusinessException(message);
            }

            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }

        accountRepository.save(account);
    }


}
