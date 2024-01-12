package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.model.Transaction;

import java.util.List;

public interface NewTransactionResponseMapper {
    NewTransactionResponseDTO toDto(Transaction transaction, Long accountNumber);

    List<NewTransactionResponseDTO> toDto(List<Transaction> transactionList, Long accountNumber);
}
