package uk.jpmorgan.example.paymentrouting.exception;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String message) {
        super(message);
    }
}
