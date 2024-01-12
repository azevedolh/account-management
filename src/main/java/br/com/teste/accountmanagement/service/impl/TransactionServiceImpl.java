package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
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
import br.com.teste.accountmanagement.util.PaginationUtils;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PageableMapper pageableMapper;

    @Autowired
    private NewTransactionResponseMapper newTransactionResponseMapper;

    @Autowired
    private TransactionResponseMapper transactionResponseMapper;

    @Autowired
    private TransactionRequestMapper transactionRequestMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private NotificationService notificationService;

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
    @Transactional
    public NewTransactionResponseDTO create(CreateTransactionRequestDTO transactionRequest, Long origin, Long originCustomer) {

        try {
            accountService.updateBalance(origin, OperationEnum.DEBITO, transactionRequest.getAmount());
            accountService.updateBalance(transactionRequest.getDestination(), OperationEnum.CREDITO, transactionRequest.getAmount());
        } catch (Exception e) {
            log.error("Erro ao realizar processo de atualização de saldo", e);
            throw new CustomBusinessException("Erro ao realizar processo de atualização de saldo.", e.getMessage());
        }

        Transaction transaction = transactionRequestMapper.toEntity(transactionRequest, origin);
        transaction = transactionRepository.save(transaction);
        NewTransactionResponseDTO newTransactionResponseDTO = newTransactionResponseMapper.toDto(transaction, origin);

        Account destinationAccount = accountService.getById(transactionRequest.getDestination());

        List<NotificationResultDTO> notificationList = treatNotifications(originCustomer, destinationAccount);

        newTransactionResponseDTO.setNotificationResult(notificationList);
        return newTransactionResponseDTO;
    }

    private List<NotificationResultDTO> treatNotifications(Long originCustomer, Account destinationAccount) {
        List<NotificationResultDTO> notificationList = new ArrayList<>();

        try {
            notificationService.sendNotification(originCustomer);
            notificationList.add(NotificationResultDTO.builder()
                    .accountType(NotificationAccountTypeEnum.ORIGIN)
                    .notificationStatus(NotificationStatusEnum.SENT)
                    .message(NotificationStatusEnum.SENT.getDescription())
                    .build());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação", e);
            notificationList.add(NotificationResultDTO.builder()
                    .accountType(NotificationAccountTypeEnum.ORIGIN)
                    .notificationStatus(NotificationStatusEnum.ERROR)
                    .message("Erro ao enviar notificação: " + e.getMessage())
                    .build());
        }

        if (!originCustomer.equals(destinationAccount.getCustomer().getId())) {
            try {
                notificationService.sendNotification(destinationAccount.getCustomer().getId());
                notificationList.add(NotificationResultDTO.builder()
                        .accountType(NotificationAccountTypeEnum.ORIGIN)
                        .notificationStatus(NotificationStatusEnum.SENT)
                        .message(NotificationStatusEnum.SENT.getDescription())
                        .build());
            } catch (Exception e) {
                log.error("Erro ao enviar notificação", e);
                notificationList.add(NotificationResultDTO.builder()
                        .accountType(NotificationAccountTypeEnum.DESTINATION)
                        .notificationStatus(NotificationStatusEnum.ERROR)
                        .message("Erro ao enviar notificação: " + e.getMessage())
                        .build());
            }
        }

        return notificationList;
    }

    @Override
    @Transactional
    public NewTransactionResponseDTO cancel(CancelTransactionRequestDTO transactionRequest, Long accountId) {
        Transaction transaction = getById(transactionRequest.getId());
        Account destinationAccount = accountService.getById(transaction.getDestination().getId());

        CreateTransactionRequestDTO createTransactionRequestDTO = CreateTransactionRequestDTO.builder()
                .destination(transaction.getOrigin().getId())
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
            throw new CustomBusinessException(HttpStatus.NOT_FOUND, "Transação não encontrada");
        }

        return transactionOptional.get();
    }


}
