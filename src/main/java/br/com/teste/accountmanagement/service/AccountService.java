package br.com.teste.accountmanagement.service;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.model.Account;

import java.math.BigDecimal;

public interface AccountService {

    PageResponseDTO getAccounts(Long customerId, Integer page, Integer size, String sort);

    Account create(CreateAccountRequestDTO account);

    Account getById(Long accountId);

    void updateBalance(Long origin, Long destination, BigDecimal amount);
}
