package uk.jpmorgan.example.paymentrouting.controller;

import jakarta.validation.constraints.NotBlank;
import uk.jpmorgan.example.paymentrouting.domain.PaymentRoute;
import uk.jpmorgan.example.paymentrouting.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/route")
    public ResponseEntity<PaymentRoute> route(
            @RequestParam @NotBlank String origin,
            @RequestParam @NotBlank String destination) {

        return paymentService.processPayment(origin, destination)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}