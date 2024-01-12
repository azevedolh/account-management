package br.com.teste.accountmanagement.controller;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.PostResponseDTO;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Log4j2
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<PageResponseDTO> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "_sort", required = false) String sort,
            @RequestParam(value = "document", required = false) String document,
            @RequestParam(value = "name", required = false) String name) {
        return new ResponseEntity<PageResponseDTO>(customerService.getCustomers(name, document, page, size, sort), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> create(
            @RequestBody @Valid CreateCustomerRequestDTO customer) {
        Customer createdCustomer = customerService.create(customer);

        URI locationResource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
        log.info("Successfully created Customer with ID: " + createdCustomer.getId());
        return ResponseEntity.created(locationResource).body(PostResponseDTO.builder().id(createdCustomer.getId()).build());
    }
}
