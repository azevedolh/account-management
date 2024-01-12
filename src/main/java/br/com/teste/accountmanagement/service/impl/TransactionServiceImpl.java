package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.mapper.TransactionRequestMapper;
import br.com.teste.accountmanagement.mapper.TransactionResponseMapper;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Transaction;
import br.com.teste.accountmanagement.repository.TransactionRepository;
import br.com.teste.accountmanagement.service.AccountService;
import br.com.teste.accountmanagement.service.TransactionService;
import br.com.teste.accountmanagement.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PageableMapper pageableMapper;

    @Autowired
    private TransactionResponseMapper transactionResponseMapper;

    @Autowired
    private TransactionRequestMapper transactionRequestMapper;

    @Autowired
    private AccountService accountService;

    @Override
    public PageResponseDTO getTransactions(Long accountId, Integer page, Integer size, String sort) {
        Account account = accountService.getById(accountId);

        Sort sortProperties = PaginationUtils.getSort(sort, Sort.Direction.DESC, "createdAt");

        PageRequest pageRequest = PageRequest.of(page - 1, size, sortProperties);
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        Page<Transaction> transactionPage = transactionRepository.findAllByOriginOrDestination(pageRequest, account, account);

        if (transactionPage != null) {
            pageResponseDTO.set_pageable(pageableMapper.toDto(transactionPage));
            pageResponseDTO.set_content(transactionResponseMapper.toDto(transactionPage.getContent(), account.getId()));
        }

        return pageResponseDTO;
    }

    @Override
    public Transaction create(CreateTransactionRequestDTO transactionRequest, Long origin) {
        accountService.updateBalance(origin, transactionRequest.getDestination(), transactionRequest.getAmount());
        Transaction transaction = transactionRequestMapper.toEntity(transactionRequest, origin);
        return transactionRepository.save(transaction);
    }

    @Override
    public TransactionResponseDTO cancel(CancelTransactionRequestDTO transactionRequest, Long accountId) {
        Transaction transaction = getById(transactionRequest.getId());

        Transaction newTransaction = Transaction.builder()
                .origin(transaction.getDestination())
                .destination(transaction.getOrigin())
                .amount(transaction.getAmount())
                .status(TransactionStatusEnum.EFETIVADO)
                .build();

        newTransaction = transactionRepository.save(newTransaction);

        transaction.setStatus(TransactionStatusEnum.ANULADO);
        transaction.setReferenceTransaction(newTransaction);

        transactionRepository.save(transaction);

        return transactionResponseMapper.toDto(newTransaction, accountId);
    }

    private Transaction getById(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);

        if (transactionOptional.isEmpty()) {
            throw new RuntimeException("Transação não encontrada");
        }

        return transactionOptional.get();
    }


}
