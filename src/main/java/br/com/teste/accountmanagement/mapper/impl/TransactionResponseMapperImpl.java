package br.com.teste.accountmanagement.mapper.impl;

import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.mapper.TransactionResponseMapper;
import br.com.teste.accountmanagement.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        transactionResponseDTO.createdAt( entity.getCreatedAt() );
        transactionResponseDTO.updatedAt( entity.getUpdatedAt() );
        transactionResponseDTO.originAccount( entity.getOrigin().getId() );
        transactionResponseDTO.destinationAccount( entity.getDestination().getId() );

        if ( entity.getStatus() != null ) {
            transactionResponseDTO.status( entity.getStatus().name() );
        }

        if ( entity.getOrigin().getId().equals(accountNumber)) {
            transactionResponseDTO.type(OperationEnum.DEBITO.name());
        }

        if ( entity.getDestination().getId().equals(accountNumber)) {
            transactionResponseDTO.type(OperationEnum.CREDITO.name());
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
