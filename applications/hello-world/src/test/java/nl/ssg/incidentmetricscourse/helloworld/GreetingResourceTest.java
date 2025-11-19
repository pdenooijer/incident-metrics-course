package nl.ssg.incidentmetricscourse.helloworld;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when()
            .accept(ContentType.TEXT)
            .get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST in plain text"));
    }

    @Test
    void testHelloJsonEndpoint() {
        given()
          .when()
            .accept(ContentType.JSON)
            .get("/hello")
          .then()
            .statusCode(200)
            .body("message", is("Hello from Quarkus REST in JSON"));
    }

}
