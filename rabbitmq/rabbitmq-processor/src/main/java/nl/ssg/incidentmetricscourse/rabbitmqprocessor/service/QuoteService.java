package nl.ssg.incidentmetricscourse.rabbitmqprocessor.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import nl.ssg.incidentmetricscourse.rabbitmqprocessor.persistence.ObscuroDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RequiredArgsConstructor
public class QuoteService {
    private Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger("");

    @Inject
    ObscuroDB database;

    public int getQuote(String quoteRequest) throws QuoteServiceException {
        log.error("Getting very important value from database!");
        Integer veryImportantValue = null;
        try {
            veryImportantValue = database.loadValue();
        } catch (Exception e) {
            log.error("Something went terribly wrong!");
            log.error("Error loading value from database!");
            log.error("This should never happen!");
            throw new QuoteServiceException("Something went terribly wrong!");
        }
        log.error("Got it!");
        log.error("Determine request length");
        int quoteRequestLength = quoteRequest.length();
        log.error("Got it!");
        int quote = random.nextInt(100) + veryImportantValue.intValue() + quoteRequestLength;
        return quote;

    }


    @StandardException
    public static class QuoteServiceException extends Throwable {
    }
}
