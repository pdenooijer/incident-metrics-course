package nl.ssg.incidentmetricscourse.apigateway;

public class RandomFailureException extends RuntimeException {

    public RandomFailureException(final String message) {
        super(message);
    }
}
