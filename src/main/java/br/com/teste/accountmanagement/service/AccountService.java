package br.com.teste.accountmanagement.service;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.AccountResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.model.Account;

import java.math.BigDecimal;

public interface AccountService {

    PageResponseDTO getAccounts(Long customerId, Integer page, Integer size, String sort);

    AccountResponseDTO create(CreateAccountRequestDTO account, Long customerId);

    Account getById(Long accountId);

    void updateBalance(Long account, OperationEnum destination, BigDecimal amount) throws CustomBusinessException;
}
