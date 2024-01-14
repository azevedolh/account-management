package br.com.teste.accountmanagement.dto;

import br.com.teste.accountmanagement.dto.request.*;
import br.com.teste.accountmanagement.dto.response.*;
import br.com.teste.accountmanagement.util.DTOTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GenericDtoTest {

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassCancelTransactionRequestDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(CancelTransactionRequestDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassCreateAccountRequestDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(CreateAccountRequestDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassCreateCustomerRequestDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(CreateCustomerRequestDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassCreateTransactionRequestDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(CreateTransactionRequestDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassNotificationRequestDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(NotificationRequestDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassAccountResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(AccountResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassApiErrorResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(ApiErrorResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassCustomerResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(CustomerResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassNewTransactionResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(NewTransactionResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassNotificationResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(NotificationResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassNotificationResultDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(NotificationResultDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassPageableResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(PageableResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassPageResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(PageResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassPostResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(PostResponseDTO.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassTransactionResponseDTO() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(TransactionResponseDTO.class),
                "cannot throw exception");
    }
}
