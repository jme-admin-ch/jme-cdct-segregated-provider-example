package ch.admin.bit.jme.cdct.user;

import ch.admin.bit.jeap.security.resource.semanticAuthentication.ServletSemanticAuthorization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final ServletSemanticAuthorization jeapSemanticAuthorization;

    @Schema(description = "Get all users")
    @GetMapping()
    @PreAuthorize("hasRole('user', 'read')")
    public Collection<User> getUsers() {
        return userRepository.getAllUsers();
    }

    @Schema(description = "Get one user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user', 'read')")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        User user = userRepository.getUserById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Schema(description = "Create a new user")
    @PostMapping()
    @PreAuthorize("hasRole('user', 'write')")
    public User postUser(@RequestBody UserCreation userCreation) {
        return userRepository.saveNewUser(userCreation);
    }

}
