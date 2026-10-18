 package uk.jpmorgan.example.paymentrouting.service.impl;

import uk.jpmorgan.example.paymentrouting.config.PaymentRoutingProperties;
import uk.jpmorgan.example.paymentrouting.domain.BranchConfig;
import uk.jpmorgan.example.paymentrouting.domain.State;
import uk.jpmorgan.example.paymentrouting.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class DijkstraPaymentService implements PaymentService {

    private final PaymentRoutingProperties properties;

    public DijkstraPaymentService(PaymentRoutingProperties properties) {
        this.properties = properties;
    }

    @Override
    public String processPayment(String originBranch, String destinationBranch) {

        if (originBranch == null || originBranch.isBlank() || destinationBranch == null || destinationBranch.isBlank()) {
            throw new IllegalArgumentException("Origin and destination must not be blank.");
        }

        Map<String, BranchConfig> graph = properties.getBranches();

        if (graph == null || graph.isEmpty()) {
            throw new IllegalStateException("Payment routing graph is not configured.");
        }

        String source = originBranch.toUpperCase(Locale.ROOT);
        String target = destinationBranch.toUpperCase(Locale.ROOT);

        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            return null;
        }

        if (source.equals(target)) {
            return source;
        }

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<State> pq = new PriorityQueue<>();

        graph.keySet().forEach(node ->
                distance.put(node, Integer.MAX_VALUE));

        distance.put(source, 0);
        pq.offer(new State(source, 0));

        while (!pq.isEmpty()) {
            State current = pq.poll();

            if (current.totalCost() > distance.get(current.branch())) {
                continue;
            }

            if (current.branch().equals(target)) {
                break;
            }

            BranchConfig config = graph.get(current.branch());

            if (config == null || config.destinations() == null) {
                throw new IllegalStateException(
                        "Invalid routing configuration for branch: "
                                + current.branch());
            }

            int edgeCost = config.cost();

            if (edgeCost < 0) {
                throw new IllegalStateException(
                        "Negative routing cost for branch: "
                                + current.branch());
            }

            for (String next : config.destinations()) {
                if (next == null || !distance.containsKey(next)) {
                    throw new IllegalStateException(
                            "Unknown destination branch: " + next);
                }

                final int newDistance;

                try {
                    newDistance = Math.addExact(
                            current.totalCost(), edgeCost);
                } catch (ArithmeticException exception) {
                    throw new IllegalStateException(
                            "Payment route cost overflow detected.",
                            exception);
                }

                if (newDistance < distance.get(next)) {
                    distance.put(next, newDistance);
                    previous.put(next, current.branch());
                    pq.offer(new State(next, newDistance));
                }
            }
        }

        if (distance.get(target) == Integer.MAX_VALUE) {
            return null;
        }

        LinkedList<String> path = new LinkedList<>();
        String current = target;

        while (current != null) {
            path.addFirst(current);
            current = previous.get(current);
        }

        return path.stream()
                .collect(Collectors.joining(","));
    }
}
