package br.com.teste.accountmanagement.util;

import br.com.teste.accountmanagement.dto.response.PageableResponseDTO;
import br.com.teste.accountmanagement.enumerator.DocumentTypeEnum;
import br.com.teste.accountmanagement.enumerator.TransactionStatusEnum;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Customer;
import br.com.teste.accountmanagement.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static PageableResponseDTO generatePageable() {
        return PageableResponseDTO.builder()
                ._limit(10)
                ._offset(0L)
                ._pageNumber(1)
                ._pageElements(1)
                ._totalPages(1)
                ._totalElements(1L)
                ._moreElements(false)
                .build();
    };

    public static List<Account> generateListOfAccounts() {
        List<Account> accountList = new ArrayList<>();

        Account account1 = Account.builder()
                .id(1L)
                .agency("1234")
                .balance(new BigDecimal(10))
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customer(Customer.builder().id(1L).build())
                .build();

        Account account2 = Account.builder()
                .id(2L)
                .agency("1235")
                .balance(new BigDecimal(10))
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customer(Customer.builder().id(1L).build())
                .build();

        Account account3 = Account.builder()
                .id(3L)
                .agency("1236")
                .balance(new BigDecimal(10))
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customer(Customer.builder().id(2L).build())
                .build();

        accountList.add(account1);
        accountList.add(account2);
        accountList.add(account3);
        return accountList;
    }


    public static List<Customer> generateListOfCustomers() {
        List<Customer> customerList = new ArrayList<>();

        Customer customer1 = Customer.builder()
                .id(1L)
                .name("Teste da silva")
                .document("123456789")
                .documentType(DocumentTypeEnum.PF)
                .address("Endereço de teste, numero 1")
                .password("123456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Fulano da silva")
                .document("12345678000187")
                .documentType(DocumentTypeEnum.PJ)
                .address("Endereço de teste, numero 2")
                .password("1234567")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer customer3 = Customer.builder()
                .id(3L)
                .name("Siclano da silva")
                .document("987654321")
                .documentType(DocumentTypeEnum.PF)
                .address("Endereço de teste, numero 3")
                .password("12345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customerList.add(customer1);
        customerList.add(customer2);
        customerList.add(customer3);

        return customerList;
    }

    public static List<Transaction> generateListOfTransactions() {
        List<Transaction> transactionList = new ArrayList<>();

        Transaction transaction1 = Transaction.builder()
                .id(3L)
                .origin(Account.builder()
                        .id(1L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .destination(Account.builder()
                        .id(2L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .status(TransactionStatusEnum.EFETIVADO)
                .amount(new BigDecimal(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .origin(Account.builder()
                        .id(1L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .destination(Account.builder()
                        .id(3L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .status(TransactionStatusEnum.EFETIVADO)
                .amount(new BigDecimal(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Transaction transaction3 = Transaction.builder()
                .id(1L)
                .origin(Account.builder()
                        .id(3L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .destination(Account.builder()
                        .id(1L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .status(TransactionStatusEnum.ANULADO)
                .amount(new BigDecimal(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .referenceTransaction(transaction2)
                .build();

        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);

        return transactionList;
    }

    public static Transaction generateATransaction() {
        return Transaction.builder()
                .id(1L)
                .origin(Account.builder()
                        .id(1L)
                        .customer(Customer.builder().id(1L).build())
                        .build())
                .destination(Account.builder()
                        .id(2L)
                        .customer(Customer.builder().id(2L).build())
                        .build())
                .status(TransactionStatusEnum.EFETIVADO)
                .amount(new BigDecimal(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
