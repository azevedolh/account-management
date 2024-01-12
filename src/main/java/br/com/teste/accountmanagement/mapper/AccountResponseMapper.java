package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.response.AccountResponseDTO;
import br.com.teste.accountmanagement.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountResponseMapper extends EntityMapper<AccountResponseDTO, Account> {
}
