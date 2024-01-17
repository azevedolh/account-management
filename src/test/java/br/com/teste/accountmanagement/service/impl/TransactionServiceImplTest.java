package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.CancelTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.request.CreateTransactionRequestDTO;
import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.TransactionResponseDTO;
import br.com.teste.accountmanagement.enumerator.NotificationAccountTypeEnum;
import br.com.teste.accountmanagement.enumerator.NotificationStatusEnum;
import br.com.teste.accountmanagement.enumerator.OperationEnum;
import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.mapper.impl.NewTransactionResponseMapperImpl;
import br.com.teste.accountmanagement.mapper.impl.PageableMapperImpl;
import br.com.teste.accountmanagement.mapper.impl.TransactionRequestMapperImpl;
import br.com.teste.accountmanagement.mapper.impl.TransactionResponseMapperImpl;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.model.Transaction;
import br.com.teste.accountmanagement.repository.TransactionRepository;
import br.com.teste.accountmanagement.service.AccountService;
import br.com.teste.accountmanagement.service.NotificationService;
import br.com.teste.accountmanagement.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Mock
    private TransactionRepository repository;

    @Spy
    private PageableMapperImpl pageableMapper;

    @Spy
    private NewTransactionResponseMapperImpl newTransactionResponseMapper;

    @Spy
    private TransactionResponseMapperImpl transactionResponseMapper;

    @Spy
    private TransactionRequestMapperImpl transactionRequestMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testShouldReturnAllTransactionsWhenInvoked() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<Transaction> expectedReturn = generatePage(pageRequest);

        when(accountService.getById(any())).thenReturn(expectedReturn.getContent().get(0).getOrigin());
        when(repository.findAllByOriginOrDestination(any(PageRequest.class), any(Account.class), any(Account.class)))
                .thenReturn(expectedReturn);

        PageResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactions(
                1l,
                1l,
                1,
                10,
                null
        );

        verify(repository).findAllByOriginOrDestination(any(PageRequest.class), any(Account.class), any(Account.class));

        assertEquals(expectedReturn.getContent().size(),
                transactions.get_content().size(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getId(),
                transactions.get_content().get(0).getId(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getOrigin().getId(),
                transactions.get_content().get(0).getOriginAccount(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getDestination().getId(),
                transactions.get_content().get(0).getDestinationAccount(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getStatus().name(),
                transactions.get_content().get(0).getStatus(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getAmount(),
                transactions.get_content().get(0).getAmount(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getCreatedAt(),
                transactions.get_content().get(0).getCreatedAt(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getUpdatedAt(),
                transactions.get_content().get(0).getUpdatedAt(),
                "Should be equal");
    }

    @Test
    void testShouldThrowExceptionWhenAccountIsNotFromTheCustomer() {
        when(accountService.getById(any())).thenReturn(TestUtils.generateATransaction().getOrigin());

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.getTransactions(2l, 1l, 1, 10, null),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("não pertence ao cliente de id"), "Should be true");
    }

    @Test
    void testShouldReturnNullContentWhenPageObjectNull() {
        when(accountService.getById(any())).thenReturn(TestUtils.generateATransaction().getOrigin());
        when(repository.findAllByOriginOrDestination(any(PageRequest.class), any(Account.class), any(Account.class)))
                .thenReturn(null);

        PageResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactions(
                1l,
                1l,
                1,
                10,
                null
        );

        verify(repository).findAllByOriginOrDestination(any(PageRequest.class), any(Account.class), any(Account.class));

        assertNull(transactions.get_content(), "Should be null");
    }

    private Page<Transaction> generatePage(PageRequest pageRequest) {

        List<Transaction> transactionList = TestUtils.generateListOfTransactions();

        return new PageImpl<>(transactionList, pageRequest, transactionList.size());
    }

    @Test
    void testShouldCreateTransactionAndGetPositiveReturnFromSentNotificationWhenMethodIsInvoked() {
        Transaction expected = TestUtils.generateATransaction();

        CreateTransactionRequestDTO requestDTO = CreateTransactionRequestDTO.builder()
                .destinationAccount(2L)
                .amount(new BigDecimal(100))
                .build();

        when(accountService.getById(1L)).thenReturn(expected.getOrigin());
        when(accountService.getById(2l)).thenReturn(expected.getDestination());
        when(repository.save(any())).thenReturn(expected);
        NewTransactionResponseDTO responseDTO = transactionService.create(requestDTO, 1L, 1L);

        verify(repository).save(any());

        assertEquals(expected.getId(), responseDTO.getId(), "Should be equal");
        assertEquals("DEBITO", responseDTO.getType(), "Should be equal");
        assertEquals(1L, responseDTO.getOriginAccount(), "Should be equal");
        assertEquals(requestDTO.getDestinationAccount(), responseDTO.getDestinationAccount(), "Should be equal");
        assertEquals(requestDTO.getAmount(), responseDTO.getAmount(), "Should be equal");
        assertEquals("EFETIVADO", responseDTO.getStatus(), "Should be equal");
        assertEquals(NotificationAccountTypeEnum.ORIGIN,
                responseDTO.getNotificationResult().get(0).getAccountType(),
                "Should be equal");
        assertEquals(NotificationStatusEnum.SENT,
                responseDTO.getNotificationResult().get(0).getNotificationStatus(),
                "Should be equal");
        assertEquals(NotificationAccountTypeEnum.DESTINATION,
                responseDTO.getNotificationResult().get(1).getAccountType(),
                "Should be equal");
        assertEquals(NotificationStatusEnum.SENT,
                responseDTO.getNotificationResult().get(1).getNotificationStatus(),
                "Should be equal");
    }

    @Test
    void testShouldCreateTransactionAndGetNegativeReturnFromSentNotificationWhenServiceNotWorking() {
        Transaction expected = TestUtils.generateATransaction();

        CreateTransactionRequestDTO requestDTO = CreateTransactionRequestDTO.builder()
                .destinationAccount(2L)
                .amount(new BigDecimal(100))
                .build();

        when(accountService.getById(1L)).thenReturn(expected.getOrigin());
        when(accountService.getById(2l)).thenReturn(expected.getDestination());
        doThrow(new CustomBusinessException("erro no envio da notificação"))
                .when(notificationService).sendNotification(any(), any());
        when(repository.save(any())).thenReturn(expected);
        NewTransactionResponseDTO responseDTO = transactionService.create(requestDTO, 1L, 1L);

        verify(repository).save(any());

        assertEquals(expected.getId(), responseDTO.getId(), "Should be equal");
        assertEquals("DEBITO", responseDTO.getType());
        assertEquals(1L, responseDTO.getOriginAccount());
        assertEquals(requestDTO.getDestinationAccount(), responseDTO.getDestinationAccount());
        assertEquals(requestDTO.getAmount(), responseDTO.getAmount());
        assertEquals("EFETIVADO", responseDTO.getStatus());
        assertEquals(NotificationAccountTypeEnum.ORIGIN, responseDTO.getNotificationResult().get(0).getAccountType());
        assertEquals(NotificationStatusEnum.ERROR, responseDTO.getNotificationResult().get(0).getNotificationStatus());
        assertTrue(responseDTO.getNotificationResult().get(0).getMessage().contains("erro no envio da notificação"));
        assertEquals(NotificationAccountTypeEnum.DESTINATION, responseDTO.getNotificationResult().get(1).getAccountType());
        assertEquals(NotificationStatusEnum.ERROR, responseDTO.getNotificationResult().get(1).getNotificationStatus());
        assertTrue(responseDTO.getNotificationResult().get(1).getMessage().contains("erro no envio da notificação"));
    }

    @Test
    void testShouldThrowExceptionWhenIsNotTheCustomerOfTheAccount() {
        Transaction expected = TestUtils.generateATransaction();

        CreateTransactionRequestDTO requestDTO = CreateTransactionRequestDTO.builder()
                .destinationAccount(2L)
                .amount(new BigDecimal(100))
                .build();

        when(accountService.getById(1L)).thenReturn(expected.getOrigin());

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.create(requestDTO, 1L, 2L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Não é possível realizar o pagamento"), "Should be true");
    }

    @Test
    void testShouldThrowExceptionWhenTryingToMakeATransactionWithSameAccountAsOriginAndDestination() {
        Transaction expected = TestUtils.generateATransaction();

        CreateTransactionRequestDTO requestDTO = CreateTransactionRequestDTO.builder()
                .destinationAccount(1L)
                .amount(new BigDecimal(100))
                .build();

        when(accountService.getById(1L)).thenReturn(expected.getOrigin());

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.create(requestDTO, 1L, 1L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Não é possível realizar o pagamento"),
                "Should be true");
    }

    @Test
    void testShouldThrowExceptionWhenCantUpdateBalanceOfAccounts() {
        Transaction expected = TestUtils.generateATransaction();

        CreateTransactionRequestDTO requestDTO = CreateTransactionRequestDTO.builder()
                .destinationAccount(2L)
                .amount(new BigDecimal(100))
                .build();

        doThrow(new CustomBusinessException("Erro ao realizar processo de atualização de saldo"))
                .when(accountService).updateBalance(anyLong(), any(OperationEnum.class), any(BigDecimal.class));
        when(accountService.getById(1L)).thenReturn(expected.getOrigin());

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.create(requestDTO, 1L, 1L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Erro ao realizar processo de atualização de saldo"),
                "Should be true");
    }

    @Test
    void testShouldCancelTransactionAndCreateANewOneWithTheOpositeOperation() {
        Transaction transaction = TestUtils.generateATransaction();

        when(accountService.getById(1L)).thenReturn(transaction.getOrigin());
        when(accountService.getById(2l)).thenReturn(transaction.getDestination());
        when(repository.findById(anyLong())).thenReturn(Optional.of(transaction));
        when(repository.save(any())).thenReturn(transaction);

        NewTransactionResponseDTO responseDTO = transactionService.cancel(1L, 1L, 1L);

        verify(repository, times(2)).save(transactionCaptor.capture());

        assertEquals(transaction.getDestination().getId(), transactionCaptor.getAllValues().get(0).getOrigin().getId());
        assertEquals(transaction.getOrigin().getId(), transactionCaptor.getAllValues().get(0).getDestination().getId());
        assertEquals("EFETIVADO", transactionCaptor.getAllValues().get(0).getStatus().name());
        assertEquals("ANULADO", transactionCaptor.getAllValues().get(1).getStatus().name());
        assertEquals(NotificationAccountTypeEnum.ORIGIN, responseDTO.getNotificationResult().get(0).getAccountType());
        assertEquals(NotificationStatusEnum.SENT, responseDTO.getNotificationResult().get(0).getNotificationStatus());
        assertEquals(NotificationAccountTypeEnum.DESTINATION, responseDTO.getNotificationResult().get(1).getAccountType());
        assertEquals(NotificationStatusEnum.SENT, responseDTO.getNotificationResult().get(1).getNotificationStatus());
    }

    @Test
    void testShouldThrowExceptionWhenTransactionNotFound() {
        Account account = Account.builder().customer(Customer.builder().id(1L).build()).build();
        when(accountService.getById(any())).thenReturn(account);
        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.cancel(1L, 1L, 1L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Transação não encontrada"),
                "Should be true");
    }

    @Test
    void testShouldThrowExceptionWhenIsTryingToCancelACanceledTransaction() {
        Account account = Account.builder().customer(Customer.builder().id(1L).build()).build();
        Transaction transaction = TestUtils.generateATransaction();
        transaction.setStatus(TransactionStatusEnum.ANULADO);

        when(repository.findById(anyLong())).thenReturn(Optional.of(transaction));
        when(accountService.getById(any())).thenReturn(account);

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.cancel(1L, 1L, 1L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Não é possível cancelar uma transação anulada"),
                "Should be true");
    }

    @Test
    void testShouldThrowExceptionWhenAccountAndCustomerDoesNotMatch() {
        Account account = Account.builder().customer(Customer.builder().id(2L).build()).build();
        when(accountService.getById(any())).thenReturn(account);

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> transactionService.cancel(1L, 1L, 1L),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Não é possível realizar o pagamento"),
                "Should be true");
    }
}