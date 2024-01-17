package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.model.Customer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerRequestMapper extends EntityMapper<CreateCustomerRequestDTO, Customer> {
}
