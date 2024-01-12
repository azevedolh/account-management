package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.model.Transaction;

import java.util.List;

public interface TransactionResponseMapper {
    TransactionResponseDTO toDto(Transaction transaction, Long accountNumber);

    List<TransactionResponseDTO> toDto(List<Transaction> transactionList, Long accountNumber);
}
