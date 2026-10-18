package uk.jpmorgan.example.paymentrouting.service;

import uk.jpmorgan.example.paymentrouting.domain.PaymentRoute;

import java.util.Optional;

public interface PaymentService {
    /**
     * Processes a payment by finding the optimal route between the origin
     * and destination branches based on the configured routing graph and
     * branch costs.
     *
     * @param originBranch      the branch where the payment originates
     * @param destinationBranch the branch where the payment is destined
     * @return an Optional containing the optimal PaymentRoute if a valid
     *         route exists; otherwise, {@link Optional#empty()}
     */
    Optional<PaymentRoute> processPayment(
            String originBranch,
            String destinationBranch
    );
}
