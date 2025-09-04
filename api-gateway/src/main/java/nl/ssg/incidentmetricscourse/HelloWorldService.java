package nl.ssg.incidentmetricscourse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/hello")
@RegisterRestClient(configKey = "hello-world")
public interface HelloWorldService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    HelloWorldResponse hello();
}
