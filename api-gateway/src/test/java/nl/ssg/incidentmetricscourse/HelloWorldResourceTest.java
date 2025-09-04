package nl.ssg.incidentmetricscourse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HelloWorldResourceTest {

    @InjectMock
    @RestClient
    HelloWorldService greetingService;

    @InjectMock
    RandomFailureDependency randomFailureDependency;

    @Test
    void testHelloEndpointWhenSuccessful() {
        when(greetingService.hello()).thenReturn(new HelloWorldResponse("Hello from the Mock"));
        doNothing().when(randomFailureDependency).doSomething();

        given()
            .when()
                .accept(ContentType.JSON)
                .get("/hello")
            .then()
                .statusCode(200)
                .body("message", is("Hello from the Mock"));

        verify(greetingService).hello();
        verify(randomFailureDependency).doSomething();
    }

    @Test
    void testHelloEndpointOnRandomException() {
        when(greetingService.hello()).thenReturn(new HelloWorldResponse("Hello from the Mock"));
        doThrow(RandomFailureException.class).when(randomFailureDependency).doSomething();

        given()
            .when()
                .accept(ContentType.JSON)
                .get("/hello")
            .then()
                .statusCode(500)
                .body("stack", is(RandomFailureException.class.getName()));

        verify(greetingService, times(0)).hello();
        verify(randomFailureDependency).doSomething();
    }
}
