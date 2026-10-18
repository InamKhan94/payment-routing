package uk.jpmorgan.example.paymentrouting.domain;

import java.util.List;

public record BranchConfig(int cost, List<String> destinations) {}