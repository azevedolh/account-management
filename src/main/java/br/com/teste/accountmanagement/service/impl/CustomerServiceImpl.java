package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.dto.response.CustomerResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.mapper.CustomerRequestMapper;
import br.com.teste.accountmanagement.mapper.CustomerResponseMapper;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.repository.CustomerRepository;
import br.com.teste.accountmanagement.service.CustomerService;
import br.com.teste.accountmanagement.specification.CustomerSpecifications;
import br.com.teste.accountmanagement.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PageableMapper pageableMapper;

    @Autowired
    private CustomerResponseMapper customerResponseMapper;

    @Autowired
    private CustomerRequestMapper customerRequestMapper;

    @Override
    public PageResponseDTO getCustomers(String name, String document, Integer page, Integer size, String sort) {
        Sort sortProperties = PaginationUtils.getSort(sort, Sort.Direction.DESC, "createdAt");

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
            throw new CustomBusinessException("Já existe um cliente cadastrado com este documento");
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
            throw new CustomBusinessException(HttpStatus.NOT_FOUND, "Cliente com Id " + id + " não encontrado.");
        }

        return customerOptional.get();
    }
}
