package nl.ssg.incidentmetricscourse.apigateway;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RandomFailureDependency {

    @ConfigProperty(name = "api-gateway.success-rate")
    float successRate;

    private final Random random = new Random();


    public void doSomething() {
        boolean randomFailure = random.nextFloat() > successRate;
        if (randomFailure) {
            throw new RandomFailureException("50/50 random failure");
        }
    }
}
