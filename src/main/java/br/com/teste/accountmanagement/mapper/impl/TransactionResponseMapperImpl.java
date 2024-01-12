package br.com.teste.accountmanagement.mapper.impl;

import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import br.com.teste.accountmanagement.enumerator.TransactionTypeEnum;
import br.com.teste.accountmanagement.mapper.TransactionResponseMapper;
import br.com.teste.accountmanagement.model.Transaction;
import org.springframework.stereotype.Component;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class TransactionResponseMapperImpl implements TransactionResponseMapper {

    @Override
    public TransactionResponseDTO toDto(Transaction entity, Long accountNumber) {
        if ( entity == null ) {
            return null;
        }

        TransactionResponseDTO.TransactionResponseDTOBuilder transactionResponseDTO = TransactionResponseDTO.builder();

        transactionResponseDTO.id( entity.getId() );
        transactionResponseDTO.amount( entity.getAmount() );
        if ( entity.getStatus() != null ) {
            transactionResponseDTO.status( entity.getStatus().name() );
        }
        transactionResponseDTO.createdAt( entity.getCreatedAt() );
        transactionResponseDTO.updatedAt( entity.getUpdatedAt() );

        if ( entity.getOrigin().getId().equals(accountNumber)) {
            transactionResponseDTO.type(TransactionTypeEnum.DEBITO.name());
        }

        if ( entity.getDestination().getId().equals(accountNumber)) {
            transactionResponseDTO.type(TransactionTypeEnum.CREDITO.name());
        }

        return transactionResponseDTO.build();
    }

    @Override
    public List<TransactionResponseDTO> toDto(List<Transaction> dtoList, Long accountNumber) {
        if ( dtoList == null && accountNumber != null) {
            return null;
        }

        List<TransactionResponseDTO> list = new ArrayList<TransactionResponseDTO>( dtoList.size() );
        for ( Transaction transaction : dtoList ) {
            list.add( toDto( transaction, accountNumber ) );
        }

        return list;
    }
}
