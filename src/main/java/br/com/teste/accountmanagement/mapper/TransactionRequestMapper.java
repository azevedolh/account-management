package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.model.Transaction;

public interface TransactionRequestMapper {

    Transaction toEntity(CreateTransactionRequestDTO dto, Long origin);
}
