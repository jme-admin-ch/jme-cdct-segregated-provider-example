package ch.admin.bit.jme.cdct.user;

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
import ch.admin.bit.jme.cdct.user.User;
import ch.admin.bit.jme.cdct.user.UserCreation;
import ch.admin.bit.jme.cdct.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * Test for the service against the User API requirements, which is associated with the pacticipant bit-jme-cdct-segregated-provider-service_user.
 * This test class executes Pact tests for this provider. The pacts to test are fetched from a Pact broker and have
 * been published there by the consumers of this provider. The provider tests are run as Spring Boot test i.e. we are
 * testing the provider mostly as it would run in production. We do however mock the JWKS endpoint of the external
 * authorization server with a local JWKS endpoint provided by JeapOAuth2IntegrationTestResourceConfiguration.
 */
@Provider("bit-jme-cdct-segregated-provider-service_user")
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
class UserControllerProviderTest {

    private static final String USER_ID_PARAM_NAME = "user-id";

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        // If there are no pacts there will be no context.
        if (context != null) {
            context.setTarget(new HttpTestTarget("localhost", 1235, "/"));
            userRepository.deleteAll();
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

    @State("A user with user id '1' is present")
    void initStateUserWithId1Present() {
        createUserWithId("1", "User with id 1");
    }

    /**
     * Example for a provider state definition with state parameters set by the provider.
     *
     * @return The provider state parameter definitions as map.
     */
    @State("A user is present")
    Map<String, String> initStateUserPresent() {
        User user = userRepository.saveNewUser(userCreation("user"));
        return Map.of(USER_ID_PARAM_NAME, user.getId());
    }


    /**
     * Example for a provider state definition with state parameters set by the provider.
     *
     * @param stateParameters The parameter definitions set by the consumer as map.
     */
    @State("A user with user id ${user-id} is present")
    void initStateUserWithGivenIdPresent(Map<String, String> stateParameters) {
        createUserWithId(stateParameters.get(USER_ID_PARAM_NAME), "User with given id");
    }

    @State("Several users are present")
    void initStateSeveralUsersPresent() {
        userRepository.saveNewUser(userCreation("Darth Vader"));
        userRepository.saveNewUser(userCreation("Boda Fett"));
        userRepository.saveNewUser(userCreation("Anakin Skywalker"));

    }

    @State("No user is present")
    void initStateNoUserPresent() {
        userRepository.deleteAll();
    }

    private void createUserWithId(String id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        userRepository.saveUser(user);
    }

    private UserCreation userCreation(String name) {
        UserCreation userCreation = new UserCreation();
        userCreation.setName(name);
        return userCreation;
    }

}
