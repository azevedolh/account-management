package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.response.CustomerResponseDTO;
import br.com.teste.accountmanagement.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerResponseMapper extends EntityMapper<CustomerResponseDTO, Customer> {
}
