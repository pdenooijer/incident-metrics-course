package nl.ssg.incidentmetricscourse.helloworld;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    private final MeterRegistry registry;

    GreetingResource(MeterRegistry registry) {
        this.registry = registry;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        registry.counter("hello", "type", "plain").increment();
        return "Hello from Quarkus REST in plain text";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HelloWorldResponse helloJson() {
        registry.counter("hello", "type", "json").increment();
        return new HelloWorldResponse("Hello from Quarkus REST in JSON");
    }
}
