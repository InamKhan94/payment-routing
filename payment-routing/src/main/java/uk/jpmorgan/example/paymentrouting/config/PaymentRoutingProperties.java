package uk.jpmorgan.example.paymentrouting.config;

import uk.jpmorgan.example.paymentrouting.domain.BranchConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "payment-routing")
public class PaymentRoutingProperties{

    private Map<String, BranchConfig> branches = new HashMap<>();

    public Map<String, BranchConfig> getBranches() {
         return branches;
    }

    public void setBranches(Map<String, BranchConfig> branches) {
        this.branches = branches;
    }
}
