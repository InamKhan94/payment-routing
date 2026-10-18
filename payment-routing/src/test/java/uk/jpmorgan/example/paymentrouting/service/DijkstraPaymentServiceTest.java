package uk.jpmorgan.example.paymentrouting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DijkstraPaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void shouldFindCheapestRoute() {
        String route = paymentService.processPayment("A", "D");
        assertEquals("A,C,E,D", route);
    }

    @Test
    void shouldReturnNullWhenPathDoesNotExist() {
        String route = paymentService.processPayment("F", "A");
        assertNull(route);
    }

    @Test
    void shouldReturnSameBranch() {
        String route = paymentService.processPayment("A", "A");
        assertEquals("A", route);
    }

    @Test
    void shouldThrowExceptionWhenOriginIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(" ", "D")
        );

        assertEquals("Origin and destination must not be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDestinationIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment("A", null)
        );

        assertEquals("Origin and destination must not be blank.", exception.getMessage());
    }

    @Test
    void shouldHandleLowercaseBranchNames() {
        String route = paymentService.processPayment("a", "d");
        assertEquals("A,C,E,D", route);
    }
}