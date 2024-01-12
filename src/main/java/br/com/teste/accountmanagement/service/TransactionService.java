package br.com.teste.accountmanagement.service;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.model.Transaction;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;

public interface TransactionService {

    PageResponseDTO getTransactions(Long accountId, Integer page, Integer size, String sort);

    Transaction create(CreateTransactionRequestDTO transaction, Long origin);

    TransactionResponseDTO cancel(CancelTransactionRequestDTO transaction, Long accountId);

}
