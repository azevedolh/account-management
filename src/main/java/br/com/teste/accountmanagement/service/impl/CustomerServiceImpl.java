package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.mapper.CustomerRequestMapper;
import br.com.teste.accountmanagement.mapper.CustomerResponseMapper;
import br.com.teste.accountmanagement.mapper.PageableMapper;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.repository.CustomerRepository;
import br.com.teste.accountmanagement.service.CustomerService;
import br.com.teste.accountmanagement.specification.CustomerSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Sort sortProperties = getSort(sort);

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
    public Customer create(CreateCustomerRequestDTO customerRequest) {
        Optional<Customer> existingCustomer = customerRepository.findByDocument(customerRequest.getDocument());

        if (existingCustomer.isPresent()) {
            throw new RuntimeException("Já existe um cliente cadastrado com este documento");
        }

        Customer customer = customerRequestMapper.toEntity(customerRequest);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        customer.setPassword(bCryptPasswordEncoder.encode(customerRequest.getPassword()));

        return customerRepository.save(customer);
    }

    @Override
    public Customer getById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Cliente com Id " + id + " não encontrado.");
        }

        return customerOptional.get();
    }

    private Sort getSort(String sort) {
        if (sort != null) {
            List<Order> orders = new ArrayList<>();

            String[] sortArray = sort.split(",");

            for (int i = 0; i < sortArray.length; i = i + 2) {
                Order order = new Order(Sort.Direction.valueOf(sortArray[i + 1].toUpperCase()), sortArray[1]);
                orders.add(order);
            }

            return Sort.by(orders);
        }

        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
