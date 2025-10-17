package org.acme.rabbitmq.processor;

import java.util.Random;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import org.acme.rabbitmq.model.Quote;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.jboss.logging.Logger;

/**
 * A bean consuming data from the "request" RabbitMQ queue and giving out a random quote.
 * The result is pushed to the "quotes" RabbitMQ exchange.
 */
@ApplicationScoped
public class QuoteProcessor {
    private static final Logger LOG = Logger.getLogger(QuoteProcessor.class);

    @Inject
    @ConfigProperty(name = "processor.duration.min")
    int minDuration;

    @Inject
    @ConfigProperty(name = "processor.duration.max")
    int maxDuration;

    @Inject
    @ConfigProperty(name = "processor.success-rate")
    float successRate;

    private final Random random = new Random();

    @Incoming("quote-requests")
    @Outgoing("quotes")
    @Blocking
    public Quote process(String quoteRequest) throws InterruptedException {
        // simulate some hard working task
        int wait = Math.round(minDuration + random.nextFloat() * (maxDuration - minDuration));
        boolean willSucceed = random.nextFloat() < successRate;
        LOG.info("Processing request '" + quoteRequest + "', delay is " + wait + ", success-rate is " + successRate + ", willSucceed = " + willSucceed);
        Thread.sleep(wait);
        if (!willSucceed) {
            throw new RuntimeException("Failing " + quoteRequest);
        }
        return new Quote(quoteRequest, random.nextInt(100), wait);
    }
}
