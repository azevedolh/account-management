package br.com.teste.accountmanagement.service;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;

public interface TransactionService {

    PageResponseDTO getTransactions(Long customerId, Long accountId, Integer page, Integer size, String sort);

    NewTransactionResponseDTO create(CreateTransactionRequestDTO transaction, Long origin, Long originCustomer);

    NewTransactionResponseDTO cancel(Long transactionId, Long accountId);

}
