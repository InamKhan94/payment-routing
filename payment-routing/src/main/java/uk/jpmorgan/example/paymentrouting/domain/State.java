package uk.jpmorgan.example.paymentrouting.domain;

public record State(
        String branch,
        int totalCost
) implements Comparable<State> {

    @Override
    public int compareTo(State other) {
        return Integer.compare(this.totalCost, other.totalCost);
    }
}
