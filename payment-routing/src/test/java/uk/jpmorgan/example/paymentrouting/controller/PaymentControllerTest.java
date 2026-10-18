package uk.jpmorgan.example.paymentrouting.controller;

import uk.jpmorgan.example.paymentrouting.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
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
    void shouldReturnNotFoundWithProblemDetailWhenRouteDoesNotExist()
            throws Exception {

        when(paymentService.processPayment("F", "A"))
                .thenReturn(null);

        mockMvc.perform(get("/payments/route")
                        .param("origin", "F")
                        .param("destination", "A"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Route Not Found"))
                .andExpect(jsonPath("$.detail").value(
                        "No valid payment route exists for the supplied origin and destination branches."))
                .andExpect(jsonPath("$.instance").value("/payments/route"));

        verify(paymentService).processPayment("F", "A");
    }

    @Test
    void shouldReturnBadRequestWithProblemDetailWhenOriginIsBlank()
            throws Exception {

        mockMvc.perform(get("/payments/route")
                        .param("origin", "")
                        .param("destination", "D"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value(
                        "Invalid request. Please provide valid origin and destination branches."))
                .andExpect(jsonPath("$.instance").value("/payments/route"));

        verifyNoInteractions(paymentService);
    }

    @Test
    void shouldReturnBadRequestWithProblemDetailWhenDestinationIsBlank()
            throws Exception {

        mockMvc.perform(get("/payments/route")
                        .param("origin", "A")
                        .param("destination", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value(
                        "Invalid request. Please provide valid origin and destination branches."))
                .andExpect(jsonPath("$.instance").value("/payments/route"));

        verifyNoInteractions(paymentService);
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