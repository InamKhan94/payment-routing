package uk.jpmorgan.example.paymentrouting.controller;

import jakarta.validation.constraints.NotBlank;
import uk.jpmorgan.example.paymentrouting.exception.RouteNotFoundException;
import uk.jpmorgan.example.paymentrouting.service.PaymentService;
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
    public String route(
            @RequestParam @NotBlank String origin,
            @RequestParam @NotBlank String destination) {

        String route = paymentService.processPayment(origin, destination);

        if (route == null) {
            throw new RouteNotFoundException(
                    "No valid payment route exists for the supplied origin and destination branches."
            );
        }

        return route;
    }
}
