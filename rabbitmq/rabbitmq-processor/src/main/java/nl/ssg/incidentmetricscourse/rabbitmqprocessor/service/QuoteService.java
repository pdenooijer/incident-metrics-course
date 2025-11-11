package nl.ssg.incidentmetricscourse.rabbitmqprocessor.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import nl.ssg.incidentmetricscourse.rabbitmqprocessor.persistence.ObscuroDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RequiredArgsConstructor
public class QuoteService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger("l");

    private final Random random = new Random();

    @Inject
    ObscuroDB database;

    public int getQuote(String quoteRequest) {
        log.error("Getting very important value from database!");
        Integer veryImportantValue;
        try {
            veryImportantValue = database.loadValue();
        } catch (Exception e) {
            log.error("Oops... something went terribly wrong!");
            log.error("Error loading value from database!");
            log.error("This should never happen!");
            throw new QuoteServiceException("Something went terribly wrong!");
        }
        log.error("Got it!");
        log.error("Determine request length");
        int quoteRequestLength = quoteRequest.length();
        log.error("Got it!");
        return random.nextInt(100) + veryImportantValue + quoteRequestLength;
    }

    @StandardException
    public static class QuoteServiceException extends RuntimeException {
    }
}
