package uk.jpmorgan.example.paymentrouting.controller;

import uk.jpmorgan.example.paymentrouting.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldReturnCheapestRoute() throws Exception {
        when(paymentService.processPayment("A", "D"))
                .thenReturn("A,C,E,D");

        mockMvc.perform(get("/payments/route")
                        .param("origin", "A")
                        .param("destination", "D"))
                .andExpect(status().isOk())
                .andExpect(content().string("A,C,E,D"));

        verify(paymentService).processPayment("A", "D");
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist()
            throws Exception {

        when(paymentService.processPayment("F", "A"))
                .thenReturn(null);

        mockMvc.perform(get("/payments/route")
                        .param("origin", "F")
                        .param("destination", "A"))
                .andExpect(status().isNotFound());

        verify(paymentService).processPayment("F", "A");
    }

    @Test
    void shouldReturnBadRequestWhenOriginIsBlank()
            throws Exception {

        mockMvc.perform(get("/payments/route")
                        .param("origin", "")
                        .param("destination", "D"))
                .andExpect(status().isBadRequest());

        verify(paymentService, never())
                .processPayment("", "D");
    }

    @Test
    void shouldReturnBadRequestWhenDestinationIsBlank()
            throws Exception {

        mockMvc.perform(get("/payments/route")
                        .param("origin", "A")
                        .param("destination", " "))
                .andExpect(status().isBadRequest());

        verify(paymentService, never())
                .processPayment("A", " ");
    }

    @Test
    void shouldReturnBadRequestWhenOriginIsMissing()
            throws Exception {

        mockMvc.perform(get("/payments/route")
                        .param("destination", "D"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void shouldReturnBadRequestWhenDestinationIsMissing()
            throws Exception {

        mockMvc.perform(get("/payments/route")
                        .param("origin", "A"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void shouldReturnSameBranchRoute() throws Exception {
        when(paymentService.processPayment("A", "A"))
                .thenReturn("A");

        mockMvc.perform(get("/payments/route")
                        .param("origin", "A")
                        .param("destination", "A"))
                .andExpect(status().isOk())
                .andExpect(content().string("A"));

        verify(paymentService).processPayment("A", "A");
    }
}
