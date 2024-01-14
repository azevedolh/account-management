package br.com.teste.accountmanagement.mapper;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.enumerator.DocumentTypeEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.model.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerRequestMapper extends EntityMapper<CreateCustomerRequestDTO, Customer> {

    @BeforeMapping
    default void beforeToEntityMapping(CreateCustomerRequestDTO dto, @MappingTarget Customer customer) {
        if (!DocumentTypeEnum.isValid(dto.getDocumentType())) {
            throw new CustomBusinessException("Tipo de documento inválido. Valores permitidos: PF e PJ");
        }
    }
}
