package nl.ssg.incidentmetricscourse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST in plain text";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HelloWorldResponse helloJson() {
        return new HelloWorldResponse("Hello from Quarkus REST in JSON");
    }
}
