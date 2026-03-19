package ch.admin.bit.jeap.jme.processcontext.test;

import ch.admin.bit.jeap.jme.test.BootServiceSpringIntegrationTestBase;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Slf4j
public class CdctSegregatedProviderExampleIT extends BootServiceSpringIntegrationTestBase {

    private static final String AUTH_BASE_URL = "http://localhost:8180/jme-cdct-segregated-auth-scs";
    private static final String APP_BASE_URL = "http://localhost:8080/jme-cdct-segregated-provider-service";

    @BeforeAll
    static void startServices() throws Exception {
        startService("jme-cdct-segregated-auth-scs", AUTH_BASE_URL);
        startService("jme-cdct-segregated-provider-service", APP_BASE_URL);
    }

    @Test
    void testAuthenticatedApiCalls() {
        String accessToken = fetchAccessToken(AUTH_BASE_URL, "jme-cdct-segregated-consumer-service", "secret");
        RequestSpecification authRequest = given()
                .header("Authorization", "Bearer " + accessToken)
                .baseUri(APP_BASE_URL);

        // Create a new user
        String userId = authRequest
                .contentType("application/json")
                .body("{\"name\": \"Test User\"}")
                .when()
                .post("/api/user")
                .then()
                .statusCode(200)
                .body("name", equalTo("Test User"))
                .body("id", notNullValue())
                .extract().path("id");

        // Get the created user by id
        authRequest
                .when()
                .get("/api/user/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", equalTo("Test User"));

        // Get all users
        authRequest
                .when()
                .get("/api/user")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));

        // Create a new task
        String taskId = authRequest
                .contentType("application/json")
                .body("{\"title\": \"Test Task\", \"content\": \"Task content\"}")
                .when()
                .post("/api/task")
                .then()
                .statusCode(200)
                .body("title", equalTo("Test Task"))
                .body("content", equalTo("Task content"))
                .body("id", notNullValue())
                .extract().path("id");

        // Get the created task by id
        authRequest
                .when()
                .get("/api/task/" + taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("title", equalTo("Test Task"));

        // Get all tasks
        authRequest
                .when()
                .get("/api/task")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }
}
