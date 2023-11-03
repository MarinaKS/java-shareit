package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUsers() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        List<User> users = userService.getUsers();

        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
    }

    @Test
    public void testGetUser() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Long userId = user.getId();

        User retrievedUser = userService.getUser(userId);

        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testAddUser() {
        User user = new User();
        user.setName("New User");
        user.setEmail("newuser@example.com");

        User addedUser = userService.addUser(user);
        Long userId = addedUser.getId();

        assertNotNull(userId);
        assertTrue(userRepository.existsById(userId));
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        user.setName("Updated User");
        user.setEmail("updateduser@example.com");

        User updatedUser = userService.updateUser(user);
        Long userId = updatedUser.getId();

        assertNotNull(userId);
        User retrievedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals("Updated User", retrievedUser.getName());
        assertEquals("updateduser@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Long userId = user.getId();

        userService.deleteUser(userId);

        assertFalse(userRepository.existsById(userId));
    }
}