package ch.admin.bit.jme.cdct.task;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.AllowOverridePactUrl;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import ch.admin.bit.jeap.security.test.jws.JwsBuilder;
import ch.admin.bit.jeap.security.test.resource.configuration.JeapOAuth2IntegrationTestResourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Test for the service against the User API requirements, which is associated with the pacticipant bit-jme-cdct-segregated-provider-service_task.
 * This test class executes Pact tests for this provider. The pacts to test are fetched from a Pact broker and have
 * been published there by the consumers of this provider. The provider tests are run as Spring Boot test i.e. we are
 * testing the provider mostly as it would run in production. We do however mock the JWKS endpoint of the external
 * authorization server with a local JWKS endpoint provided by JeapOAuth2IntegrationTestResourceConfiguration.
 */
@Provider("bit-jme-cdct-segregated-provider-service_task")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "jeap.security.oauth2.resourceserver.authorization-server.issuer=" + JwsBuilder.DEFAULT_ISSUER,
                "jeap.security.oauth2.resourceserver.authorization-server.jwk-set-uri=http://localhost:1235/jme-cdct-segregated-provider-service/.well-known/jwks.json"
        }
)
@Import(JeapOAuth2IntegrationTestResourceConfiguration.class)
@PactBroker
@AllowOverridePactUrl // Allow externally specified pact url to override other consumer version selectors.
@IgnoreNoPactsToVerify // For providers that do not yet or no longer have a consumer. Remove annotation when at least one consumer pact is expected to be available.
class TaskControllerProviderTest {

    private static final String TASK_ID_PARAM_NAME = "task-id";

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        // If there are no pacts there will be no context.
        if (context != null) {
            context.setTarget(new HttpTestTarget("localhost", 1235, "/"));
            taskRepository.deleteAll();
        }
    }

    // This will verify this provider against all the pacts defined in the pact broker for this provider
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testPacts(PactVerificationContext context) {
        // If there are no pacts there will be no context.
        if (context != null) {
            context.verifyInteraction();
        }
    }

    // Some (most) interactions need the provider to be in a certain state for a successful test of the interaction. For each
    // such provider state a method needs to be provided that puts the provider in the required state. The state names defined
    // here and the state names used by the consumers in their interaction specifications must match, obviously.

    @State("A task with task id '1' is present")
    void initStateTaskWithId1Present() {
        createTaskWithId("1", "Task with id 1", "Do this and that", "private", ZonedDateTime.now());
    }

    /**
     * Example for a provider state definition with state parameters set by the provider.
     *
     * @return The provider state parameter definitions as map.
     */
    @State("A task is present")
    Map<String, String> initStateTaskPresent() {
        Task task = taskRepository.saveNewTask(taskCreation("task", "this is a task", "important"));
        return Map.of(TASK_ID_PARAM_NAME, task.getId());
    }


    /**
     * Example for a provider state definition with state parameters set by the provider.
     *
     * @param stateParameters The parameter definitions set by the consumer as map.
     */
    @State("A task with task id ${task-id} is present")
    void initStateTaskWithGivenIdPresent(Map<String, String> stateParameters) {
        createTaskWithId(stateParameters.get(TASK_ID_PARAM_NAME), "Task with given id", "Do something", "at home", ZonedDateTime.now());
    }

    @State("Several tasks are present")
    void initStateSeveralTasksPresent() {
        taskRepository.saveNewTask(taskCreation("first task", "this is the first task", null));
        taskRepository.saveNewTask(taskCreation("second task", "this is the second task", "super important"));
        taskRepository.saveNewTask(taskCreation("third task", "this is the third task", "important"));
        taskRepository.saveNewTask(taskCreation("fourth task", "this is the fourth task", null));
    }

    @State("No task is present")
    void initStateNoTaskPresent() {
        taskRepository.deleteAll();
    }

    private void createTaskWithId(String id, String title, String content, String tag, ZonedDateTime createdAt) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setContent(content);
        task.setTag(tag);
        task.setCreatedAt(createdAt);
        taskRepository.saveTask(task);
    }

    private TaskCreation taskCreation(String title, String content, String tag) {
        TaskCreation taskCreation = new TaskCreation();
        taskCreation.setTitle(title);
        taskCreation.setContent(content);
        taskCreation.setTag(tag);
        return taskCreation;
    }

}
