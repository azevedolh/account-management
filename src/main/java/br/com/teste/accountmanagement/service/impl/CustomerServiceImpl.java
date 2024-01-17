package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.dto.response.CustomerResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.enumerator.DocumentTypeEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.mapper.CustomerRequestMapper;
import br.com.teste.accountmanagement.mapper.CustomerResponseMapper;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.repository.CustomerRepository;
import br.com.teste.accountmanagement.service.CustomerService;
import br.com.teste.accountmanagement.specification.CustomerSpecifications;
import br.com.teste.accountmanagement.util.MessageUtil;
import br.com.teste.accountmanagement.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.teste.accountmanagement.util.ConstantUtil.SORT_BY_CREATED_AT;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private PageableMapper pageableMapper;
    private CustomerResponseMapper customerResponseMapper;
    private CustomerRequestMapper customerRequestMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               PageableMapper pageableMapper,
                               CustomerResponseMapper customerResponseMapper,
                               CustomerRequestMapper customerRequestMapper) {
        this.customerRepository = customerRepository;
        this.pageableMapper = pageableMapper;
        this.customerResponseMapper = customerResponseMapper;
        this.customerRequestMapper = customerRequestMapper;
    }

    @Override
    public PageResponseDTO getCustomers(String name, String document, Integer page, Integer size, String sort) {
        Sort sortProperties = PaginationUtil.getSort(sort, Sort.Direction.DESC, SORT_BY_CREATED_AT);

        PageRequest pageRequest = PageRequest.of(page - 1, size, sortProperties);
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        Page<Customer> customerPage = customerRepository.findAll(CustomerSpecifications.generateDinamic(name, document), pageRequest);

        if (customerPage != null) {
            pageResponseDTO.set_pageable(pageableMapper.toDto(customerPage));
            pageResponseDTO.set_content(customerResponseMapper.toDto(customerPage.getContent()));
        }

        return pageResponseDTO;
    }

    @Override
    public CustomerResponseDTO create(CreateCustomerRequestDTO customerRequest) {
        Optional<Customer> existingCustomer = customerRepository.findByDocument(customerRequest.getDocument());

        if (existingCustomer.isPresent()) {
            String message = MessageUtil.getMessage("customer.already.exists");
            throw new CustomBusinessException(message);
        }

        if (!DocumentTypeEnum.isValid(customerRequest.getDocumentType())) {
            String message = MessageUtil.getMessage("customer.document.type");
            String details = MessageUtil.getMessage("customer.document.type.details");
            throw new CustomBusinessException(message, details);
        }

        Customer customer = customerRequestMapper.toEntity(customerRequest);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        customer.setPassword(bCryptPasswordEncoder.encode(customerRequest.getPassword()));

        customer = customerRepository.save(customer);
        return customerResponseMapper.toDto(customer);
    }

    @Override
    public Customer getById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isEmpty()) {
            String message = MessageUtil.getMessage("customer.not.found", id.toString());
            throw new CustomBusinessException(HttpStatus.NOT_FOUND, message);
        }

        return customerOptional.get();
    }
}
