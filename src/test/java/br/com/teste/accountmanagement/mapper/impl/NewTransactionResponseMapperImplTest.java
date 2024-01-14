package br.com.teste.accountmanagement.mapper.impl;

import br.com.teste.accountmanagement.dto.response.NewTransactionResponseDTO;
import br.com.teste.accountmanagement.model.Account;
import br.com.teste.accountmanagement.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.teste.accountmanagement.enumerator.TransactionStatusEnum.EFETIVADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NewTransactionResponseMapperImplTest {
    private NewTransactionResponseMapperImpl mapper;

    @BeforeEach
    private void setUp() {
        mapper = new NewTransactionResponseMapperImpl();
    }

    @Test
    void TestShouldConvertFromTransactionToNewTransactionResponseDTO() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .origin(Account.builder()
                        .id(1L)
                        .build())
                .destination(Account.builder()
                        .id(2L)
                        .build())
                .amount(new BigDecimal(1000))
                .status(EFETIVADO)
                .createdAt(LocalDateTime.of(2024, 1, 13, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 13, 10, 0, 0))
                .build();

        NewTransactionResponseDTO convertedValue = mapper.toDto(transaction, 1L);

        assertEquals(1L, convertedValue.getId(), "Should be equal");
        assertEquals(1L, convertedValue.getOriginAccount(), "Should be equal");
        assertEquals(2L, convertedValue.getDestinationAccount(), "Should be equal");
        assertEquals(new BigDecimal(1000), convertedValue.getAmount(), "Should be equal");
        assertEquals("DEBITO", convertedValue.getType(), "Should be equal");
        assertEquals("EFETIVADO", convertedValue.getStatus(), "Should be equal");
    }

    @Test
    void TestShouldReturnNullWhenTransactionIsNull() {
        NewTransactionResponseDTO convertedValue = mapper.toDto(null, 1L);

        assertNull(convertedValue, "Should be null");
    }
}