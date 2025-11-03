package nl.ssg.incidentmetricscourse.apigateway;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class RandomFailureDependency {

    Random random = new Random();

    public void doSomething() {
        if (random.nextBoolean()) {
            throw new RandomFailureException("50/50 random failure");
        }
    }
}
