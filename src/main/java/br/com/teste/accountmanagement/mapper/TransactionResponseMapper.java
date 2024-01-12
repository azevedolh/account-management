package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.AccountResponseDTO;
import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Transaction;
import org.aspectj.lang.annotation.After;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface TransactionResponseMapper {
    TransactionResponseDTO toDto(Transaction transaction, Long accountNumber);

     List<TransactionResponseDTO> toDto(List<Transaction> transactionList, Long accountNumber);
}
