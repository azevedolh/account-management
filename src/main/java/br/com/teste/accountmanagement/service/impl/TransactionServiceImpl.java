package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.NotificationResultDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.enumerator.NotificationAccountTypeEnum;
import br.com.teste.accountmanagement.enumerator.NotificationStatusEnum;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.mapper.TransactionRequestMapper;
import br.com.teste.accountmanagement.mapper.NewTransactionResponseMapper;
import br.com.teste.accountmanagement.mapper.TransactionResponseMapper;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Transaction;
import br.com.teste.accountmanagement.repository.TransactionRepository;
import br.com.teste.accountmanagement.service.AccountService;
import br.com.teste.accountmanagement.service.NotificationService;
import br.com.teste.accountmanagement.service.TransactionService;
import br.com.teste.accountmanagement.util.MessageUtil;
import br.com.teste.accountmanagement.util.PaginationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.teste.accountmanagement.util.ConstantUtil.SORT_BY_CREATED_AT;

@Log4j2
@Service
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;
    private PageableMapper pageableMapper;
    private NewTransactionResponseMapper newTransactionResponseMapper;
    private TransactionResponseMapper transactionResponseMapper;
    private TransactionRequestMapper transactionRequestMapper;
    private AccountService accountService;
    private NotificationService notificationService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  PageableMapper pageableMapper,
                                  NewTransactionResponseMapper newTransactionResponseMapper,
                                  TransactionResponseMapper transactionResponseMapper,
                                  TransactionRequestMapper transactionRequestMapper,
                                  AccountService accountService,
                                  NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.pageableMapper = pageableMapper;
        this.newTransactionResponseMapper = newTransactionResponseMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionRequestMapper = transactionRequestMapper;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @Override
    public PageResponseDTO getTransactions(Long customerId, Long accountId, Integer page, Integer size, String sort) {
        Account account = accountService.getById(accountId);

        if (!account.getCustomer().getId().equals(customerId)) {
            String message = MessageUtil.getMessage("account.customer.unmatched",
                    accountId.toString(), customerId.toString());
            throw new CustomBusinessException(message);
        }

        Sort sortProperties = PaginationUtil.getSort(sort, Sort.Direction.DESC, SORT_BY_CREATED_AT);

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
    @Transactional
    public NewTransactionResponseDTO create(CreateTransactionRequestDTO transactionRequest, Long origin, Long originCustomer) {
        Account account = accountService.getById(origin);

        if (!account.getCustomer().getId().equals(originCustomer)) {
            String message = MessageUtil.getMessage("transaction.not.permited");
            String details = MessageUtil.getMessage(
                    "account.customer.unmatched",
                    origin.toString(),
                    originCustomer.toString()
            );
            throw new CustomBusinessException(message, details);
        }

        if (transactionRequest.getDestinationAccount().equals(origin)) {
            String message = MessageUtil.getMessage("transaction.not.permited");
            String details = MessageUtil.getMessage("transaction.origin.destination");
            throw new CustomBusinessException(message, details);
        }

        try {
            accountService.updateBalance(origin, OperationEnum.DEBITO, transactionRequest.getAmount());
            accountService.updateBalance(transactionRequest.getDestinationAccount(), OperationEnum.CREDITO, transactionRequest.getAmount());
        } catch (Exception e) {
            log.error("Erro ao realizar processo de atualização de saldo", e);
            String message = MessageUtil.getMessage("transaction.balance.update");
            throw new CustomBusinessException(message, e.getMessage());
        }

        Transaction transaction = transactionRequestMapper.toEntity(transactionRequest, origin);
        transaction = transactionRepository.save(transaction);
        NewTransactionResponseDTO newTransactionResponseDTO = newTransactionResponseMapper.toDto(transaction, origin);

        Account destinationAccount = accountService.getById(transactionRequest.getDestinationAccount());

        List<NotificationResultDTO> notificationList = treatNotifications(
                originCustomer,
                destinationAccount.getCustomer().getId(),
                transactionRequest.getAmount()
        );

        newTransactionResponseDTO.setNotificationResult(notificationList);
        return newTransactionResponseDTO;
    }

    private List<NotificationResultDTO> treatNotifications(Long originCustomer, Long destinationCustomer, BigDecimal amount) {
        List<NotificationResultDTO> notificationList = new ArrayList<>();

        try {
            String message = MessageUtil.getMessage("transaction.origin.notification", amount.toString());
            notificationService.sendNotification(originCustomer, message);
            notificationList.add(NotificationResultDTO.builder()
                    .accountType(NotificationAccountTypeEnum.ORIGIN)
                    .notificationStatus(NotificationStatusEnum.SENT)
                    .message(message)
                    .build());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação", e);
            String message = MessageUtil.getMessage("transaction.notification.error", e.getMessage());
            notificationList.add(NotificationResultDTO.builder()
                    .accountType(NotificationAccountTypeEnum.ORIGIN)
                    .notificationStatus(NotificationStatusEnum.ERROR)
                    .message(message)
                    .build());
        }

        try {
            String message = MessageUtil.getMessage("transaction.destination.notification", amount.toString());
            notificationService.sendNotification(destinationCustomer,message);
            notificationList.add(NotificationResultDTO.builder()
                    .accountType(NotificationAccountTypeEnum.DESTINATION)
                    .notificationStatus(NotificationStatusEnum.SENT)
                    .message(message)
                    .build());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação", e);
            String message = MessageUtil.getMessage("transaction.notification.error", e.getMessage());
            notificationList.add(NotificationResultDTO.builder()
                    .accountType(NotificationAccountTypeEnum.DESTINATION)
                    .notificationStatus(NotificationStatusEnum.ERROR)
                    .message(message)
                    .build());
        }

        return notificationList;
    }

    @Override
    @Transactional
    public NewTransactionResponseDTO cancel(Long transactionId, Long accountId, Long customerId) {

        Account account = accountService.getById(accountId);

        if (!account.getCustomer().getId().equals(customerId)) {
            String message = MessageUtil.getMessage("transaction.not.permited");
            String details = MessageUtil.getMessage(
                    "account.customer.unmatched",
                    accountId.toString(),
                    customerId.toString()
            );
            throw new CustomBusinessException(message, details);
        }

        Transaction transaction = getById(transactionId);

        if (TransactionStatusEnum.ANULADO.equals(transaction.getStatus())) {
            String message = MessageUtil.getMessage("transaction.cancelled.error");
            throw new CustomBusinessException(message);
        }

        Account destinationAccount = accountService.getById(transaction.getDestination().getId());

        CreateTransactionRequestDTO createTransactionRequestDTO = CreateTransactionRequestDTO.builder()
                .destinationAccount(transaction.getOrigin().getId())
                .amount(transaction.getAmount())
                .build();

        NewTransactionResponseDTO newTransactionResponseDTO = create(
                createTransactionRequestDTO,
                destinationAccount.getId(),
                destinationAccount.getCustomer().getId()
        );

        transaction.setStatus(TransactionStatusEnum.ANULADO);
        transaction.setReferenceTransaction(Transaction.builder().id(newTransactionResponseDTO.getId()).build());

        transactionRepository.save(transaction);

        return newTransactionResponseDTO;
    }

    private Transaction getById(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);

        if (transactionOptional.isEmpty()) {
            String message = MessageUtil.getMessage("transaction.not.found");
            throw new CustomBusinessException(HttpStatus.NOT_FOUND, message);
        }

        return transactionOptional.get();
    }
}
