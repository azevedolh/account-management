package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountRequestMapper extends EntityMapper<CreateAccountRequestDTO, Account> {

    @Mapping(target = "isActive", constant = "true")
    Account toEntity(CreateAccountRequestDTO dto);
}
