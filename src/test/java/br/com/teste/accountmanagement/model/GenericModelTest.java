package br.com.teste.accountmanagement.model;

import br.com.teste.accountmanagement.util.DTOTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GenericModelTest {

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassAccount() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(Account.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassCustomer() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(Customer.class),
                "cannot throw exception");
    }

    @Test
    public void shouldExecuteGetterAndSetterConstructorsWithClassTransaction() {
        assertDoesNotThrow(() -> DTOTester.executeGetterSetterConstructor(Transaction.class),
                "cannot throw exception");
    }
}
