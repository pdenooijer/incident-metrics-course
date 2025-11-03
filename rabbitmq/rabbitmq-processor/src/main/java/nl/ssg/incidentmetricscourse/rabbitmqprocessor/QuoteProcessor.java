package nl.ssg.incidentmetricscourse.rabbitmqprocessor;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Random;
import nl.ssg.incidentmetricscourse.rabbitmqmodel.Quote;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
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

    @Inject
    QuoteFactory quoteFactory;


    private final Random random = new Random();

    @Incoming("quote-requests")
    @Outgoing("quotes")
    @Blocking
    public Quote process(String quoteRequest) throws InterruptedException {
        // simulate some hard working task
        int wait = Math.round(minDuration + random.nextFloat() * (maxDuration - minDuration));
        LOG.info("Processing request '" + quoteRequest + "', delay is " + wait + ", success-rate is " + successRate);
        Thread.sleep(wait);

        Integer quote =  Flakyness.<Integer>withRate(successRate)
            .eitherGet(() -> quoteFactory.getQuote(quoteRequest))
            .or(() -> {
                throw new RuntimeException("Failing " + quoteRequest);
            });

        return new Quote(quoteRequest, quote, wait);
    }
}
