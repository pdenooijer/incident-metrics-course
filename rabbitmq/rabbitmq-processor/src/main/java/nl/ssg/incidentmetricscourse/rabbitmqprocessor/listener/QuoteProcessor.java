package nl.ssg.incidentmetricscourse.rabbitmqprocessor.listener;

import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import nl.ssg.incidentmetricscourse.rabbitmqmodel.Quote;
import nl.ssg.incidentmetricscourse.rabbitmqprocessor.Flakyness;
import nl.ssg.incidentmetricscourse.rabbitmqprocessor.service.QuoteService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

/**
 * A bean consuming data from the "request" RabbitMQ queue and giving out a random quote.
 * The result is pushed to the "quotes" RabbitMQ exchange.
 */
@Slf4j
@ApplicationScoped
public class QuoteProcessor {

    @ConfigProperty(name = "processor.duration.min")
    int minDuration;

    @ConfigProperty(name = "processor.duration.max")
    int maxDuration;

    @ConfigProperty(name = "processor.success-rate")
    float successRate;

    @Inject
    MeterRegistry registry;

    @Inject
    QuoteService quoteService;

    private final Random random = new Random();

    @Incoming("quote-requests")
    @Outgoing("quotes")
    @Blocking
    public Quote process(String quoteRequest) throws InterruptedException {
        // simulate some hard working task
        int wait = Math.round(minDuration + random.nextFloat() * (maxDuration - minDuration));
        log.info("Processing request '" + quoteRequest + "', delay is " + wait + ", success-rate is " + successRate);
        registry.counter("quote-requests").increment();

        Thread.sleep(wait);

        Integer quote;
        try {
            quote = Flakyness.<Integer>withRate(successRate)
                .eitherGet(() -> quoteService.getQuote(quoteRequest))
                .or(() -> {
                    throw new RuntimeException("Failing " + quoteRequest);
                });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

        return new Quote(quoteRequest, quote, wait);
    }
}
