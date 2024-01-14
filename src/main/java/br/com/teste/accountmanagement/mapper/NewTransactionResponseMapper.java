package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.model.Transaction;

public interface NewTransactionResponseMapper {
    NewTransactionResponseDTO toDto(Transaction transaction, Long accountNumber);
}
