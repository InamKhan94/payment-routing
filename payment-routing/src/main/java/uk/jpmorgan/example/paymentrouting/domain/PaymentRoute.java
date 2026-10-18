package uk.jpmorgan.example.paymentrouting.domain;

import java.util.List;

public record PaymentRoute(List<String> path, int totalCost) { }
