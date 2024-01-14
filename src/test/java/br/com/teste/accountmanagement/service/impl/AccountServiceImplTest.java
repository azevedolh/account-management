package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.AccountResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.mapper.AccountRequestMapperImpl;
import br.com.teste.accountmanagement.mapper.AccountResponseMapperImpl;
import br.com.teste.accountmanagement.mapper.impl.PageableMapperImpl;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.repository.AccountRepository;
import br.com.teste.accountmanagement.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Mock
    private AccountRepository repository;

    @Mock
    private CustomerServiceImpl customerService;

    @Spy
    private AccountResponseMapperImpl accountResponseMapper;

    @Spy
    private AccountRequestMapperImpl accountRequestMapper;

    @Spy
    private PageableMapperImpl pageableMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testShouldReturnAllAccountsOfCustomerWhenInvoked() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<Account> expectedReturn = generatePage(pageRequest);
        when(repository.findAllByCustomerAndIsActive(any(), any(), any())).thenReturn(expectedReturn);

        PageResponseDTO<AccountResponseDTO> accounts = accountService.getAccounts(1L, 1, 10, null);

        verify(repository).findAllByCustomerAndIsActive(any(), any(), any());

        assertEquals(expectedReturn.getContent().size(),
                accounts.get_content().size(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getId(),
                accounts.get_content().get(0).getId(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getBalance(),
                accounts.get_content().get(0).getBalance(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getAgency(),
                accounts.get_content().get(0).getAgency(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getIsActive(),
                accounts.get_content().get(0).getIsActive(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getCreatedAt(),
                accounts.get_content().get(0).getCreatedAt(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getUpdatedAt(),
                accounts.get_content().get(0).getUpdatedAt(),
                "Should be equal");
    }

    @Test
    void testShouldReturnNullContentWhenPageObjectNull() {
        PageResponseDTO<AccountResponseDTO> accounts = accountService.getAccounts(1L, 1, 10, null);

        verify(repository).findAllByCustomerAndIsActive(any(), any(), any());

        assertNull(accounts.get_content(), "Should be null");
    }

    private Page<Account> generatePage(PageRequest pageRequest) {

        List<Account> accountList = TestUtils.generateListOfAccounts();

        return new PageImpl<>(accountList, pageRequest, accountList.size());
    }

    @Test
    void testShouldCreateAccountWhenMethodIsInvoked() {
        Account expected = Account.builder().id(1L).build();
        CreateAccountRequestDTO requestDTO = CreateAccountRequestDTO.builder()
                .agency("1234")
                .balance(new BigDecimal(10))
                .build();

        when(repository.save(any())).thenReturn(expected);
        AccountResponseDTO response = accountService.create(requestDTO, 1L);

        verify(repository).save(any());

        assertEquals(expected.getId(), response.getId(), "Should be equal");
    }

    @Test
    void testShouldReturnAccountWhenIdExists() {
        Account expected = Account.builder().id(1L).build();

        when(repository.findById(any())).thenReturn(Optional.of(expected));

        Account account = accountService.getById(1L);

        verify(repository).findById(any());

        assertEquals(expected.getId(), account.getId(), "Should be equal");
    }

    @Test
    void testShouldReturnExceptionWhenIdDoesntExist() {
        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.getById(1L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("não encontrada"), "Should be true");
    }

    @Test
    void testShouldUpdateBalanceAddingAmountWhenOperationIsCredit() {
        Account account = Account.builder()
                .id(1L)
                .agency("1234")
                .balance(new BigDecimal(100))
                .customer(Customer.builder().id(1L).build())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));
        accountService.updateBalance(account.getId(), OperationEnum.CREDITO, new BigDecimal(10));

        verify(repository).findById(anyLong());
        verify(repository).save(accountCaptor.capture());
        assertEquals(accountCaptor.getValue().getBalance(), new BigDecimal(110), "should be equal");
    }

    @Test
    void testShouldUpdateBalanceSubtractingAmountWhenOperationIsDebit() {
        Account account = Account.builder()
                .id(1L)
                .agency("1234")
                .balance(new BigDecimal(100))
                .customer(Customer.builder().id(1L).build())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));
        accountService.updateBalance(account.getId(), OperationEnum.DEBITO, new BigDecimal(10));

        verify(repository).findById(anyLong());
        verify(repository).save(accountCaptor.capture());
        assertEquals(accountCaptor.getValue().getBalance(), new BigDecimal(90), "should be equal");
    }

    @Test
    void testShouldReturnExceptionWhenOperationNotInformed() {
        Account account = Account.builder()
                .id(1L)
                .agency("1234")
                .balance(new BigDecimal(100))
                .customer(Customer.builder().id(1L).build())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.updateBalance(account.getId(), null, new BigDecimal(10)),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Operação não informada"),
                "Should be true");
    }

    @Test
    void testShouldReturnExceptionWhenAmountNotInformed() {
        Account account = Account.builder()
                .id(1L)
                .agency("1234")
                .balance(new BigDecimal(100))
                .customer(Customer.builder().id(1L).build())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.updateBalance(account.getId(), OperationEnum.DEBITO, null),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Valor informado para operação inválido"),
                "Should be true");
    }

    @Test
    void testShouldReturnExceptionWhenAmountIsGreaterThanBalance() {
        Account account = Account.builder()
                .id(1L)
                .agency("1234")
                .balance(new BigDecimal(100))
                .customer(Customer.builder().id(1L).build())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.updateBalance(account.getId(), OperationEnum.DEBITO, new BigDecimal(200)),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Conta não possui saldo suficiente para o pagamento"),
                "Should be true");
    }
}