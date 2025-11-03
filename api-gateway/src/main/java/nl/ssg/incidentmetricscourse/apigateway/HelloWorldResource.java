package nl.ssg.incidentmetricscourse.apigateway;

import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/hello")
public class HelloWorldResource {

    @RestClient
    HelloWorldService helloWorldService;

    private final RandomFailureDependency randomFailureDependency;

    public HelloWorldResource(final RandomFailureDependency randomFailureDependency) {
        this.randomFailureDependency = randomFailureDependency;
    }

    @GET
    @Blocking
    public HelloWorldResponse hello() {
        randomFailureDependency.doSomething();

        return helloWorldService.hello();
    }
}
