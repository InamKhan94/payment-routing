package uk.jpmorgan.example.paymentrouting.service;

public interface PaymentService {
    /**
     * Processes a payment by finding the optimal route between the origin
     * and destination branches based on the configured routing graph and
     * branch costs.
     *
     * @param originBranch      the branch where the payment originates
     * @param destinationBranch the branch where the payment is destined
     *
     */
    String processPayment(
            String originBranch,
            String destinationBranch
    );
}
