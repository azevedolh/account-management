package br.com.teste.accountmanagement.service;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.model.Customer;

public interface CustomerService {

    PageResponseDTO getCustomers(String name, String document, Integer page, Integer size, String sort);

    Customer create(CreateCustomerRequestDTO customer);

    Customer getById(Long id);
}
