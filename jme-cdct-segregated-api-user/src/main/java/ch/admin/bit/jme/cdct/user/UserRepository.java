package ch.admin.bit.jme.cdct.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepository {

    private final Map<String, User> users = new LinkedHashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserById(String id) {
        return users.get(id);
    }

    public User saveNewUser(UserCreation userToCreate) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(userToCreate.getName());
        saveUser(user);
        return user;
    }

    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public void deleteAll() {
        users.clear();
    }

}
