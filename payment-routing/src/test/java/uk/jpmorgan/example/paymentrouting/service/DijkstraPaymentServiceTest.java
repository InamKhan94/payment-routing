package uk.jpmorgan.example.paymentrouting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.jpmorgan.example.paymentrouting.domain.PaymentRoute;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DijkstraPaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void shouldFindCheapestRoute() {
        PaymentRoute route = paymentService.processPayment("A", "D")
                        .orElseThrow();

        assertEquals(List.of("A", "C", "E", "D"), route.path());
        assertEquals(35, route.totalCost());
    }

    @Test
    void shouldReturnEmptyWhenPathDoesNotExist() {
        Optional<PaymentRoute> route = paymentService.processPayment("F", "A");
        assertTrue(route.isEmpty());
    }

    @Test
    void shouldReturnSameBranch() {
        PaymentRoute route = paymentService.processPayment("A", "A")
                        .orElseThrow();

        assertEquals(List.of("A"), route.path());
        assertEquals(0, route.totalCost());
    }
    @Test
    void shouldThrowExceptionWhenOriginIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(" ", "D")
        );

        assertEquals("Origin and destination must not be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDestinationIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment("A", null)
        );

        assertEquals("Origin and destination must not be blank.", exception.getMessage()
        );
    }

    @Test
    void shouldHandleLowercaseBranchNames() {
        PaymentRoute route = paymentService.processPayment("a", "d")
                .orElseThrow();

        assertEquals(List.of("A", "C", "E", "D"), route.path());
        assertEquals(35, route.totalCost());
    }
}