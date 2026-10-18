package uk.jpmorgan.example.paymentrouting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PaymentRoutingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentRoutingApplication.class, args);
    }
}
