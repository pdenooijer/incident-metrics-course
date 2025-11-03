package org.acme.rabbitmq.processor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@ApplicationScoped
@RequiredArgsConstructor
public class QuoteFactory {
    private Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger("");

    @Inject
    ObscuroDB database;

    public int getQuote(String quoteRequest) {
        log.error("Getting very important value from database!");
        int veryImportantValue = 0;
        try {
            veryImportantValue = database.loadValue();
        } catch (Exception e) {
            log.error("Something went terribly wrong!");
            log.error("Error loading value from database!");
            log.error("This should never happen!");
        }
        log.error("Got it!");
        log.error("Determine request length");
        int quoteRequestLength = quoteRequest.length();
        log.error("Got it!");
        int quote = random.nextInt(100) + veryImportantValue + quoteRequestLength;
        return quote;

    }


}
