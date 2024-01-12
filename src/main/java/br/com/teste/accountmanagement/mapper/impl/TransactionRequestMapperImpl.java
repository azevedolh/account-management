package br.com.teste.accountmanagement.mapper.impl;

import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import br.com.teste.accountmanagement.mapper.TransactionRequestMapper;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionRequestMapperImpl implements TransactionRequestMapper {

    @Override
    public Transaction toEntity(CreateTransactionRequestDTO dto, Long origin) {
        if ( dto == null || origin == null) {
            return null;
        }

        Transaction.TransactionBuilder transaction = Transaction.builder();

        transaction.destination(Account.builder().id(dto.getDestination()).build());
        transaction.origin(Account.builder().id(origin).build());
        transaction.amount(dto.getAmount());
        transaction.status(TransactionStatusEnum.EFETIVADO);

        return transaction.build();
    }
}
