package br.com.teste.accountmanagement.mapper.impl;

import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.mapper.NewTransactionResponseMapper;
import br.com.teste.accountmanagement.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewTransactionResponseMapperImpl implements NewTransactionResponseMapper {

    @Override
    public NewTransactionResponseDTO toDto(Transaction entity, Long accountNumber) {
        if ( entity == null ) {
            return null;
        }

        NewTransactionResponseDTO.NewTransactionResponseDTOBuilder transactionResponseDTO = NewTransactionResponseDTO.builder();

        transactionResponseDTO.id( entity.getId() );
        transactionResponseDTO.amount( entity.getAmount() );
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
    public List<NewTransactionResponseDTO> toDto(List<Transaction> dtoList, Long accountNumber) {
        if ( dtoList == null && accountNumber != null) {
            return null;
        }

        List<NewTransactionResponseDTO> list = new ArrayList<NewTransactionResponseDTO>( dtoList.size() );
        for ( Transaction transaction : dtoList ) {
            list.add( toDto( transaction, accountNumber ) );
        }

        return list;
    }
}
