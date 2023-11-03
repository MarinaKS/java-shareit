package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        // Arrange
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User(1L, "John", "john@example.com"));
        expectedUsers.add(new User(2L, "Jane", "jane@example.com"));
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getUsers();

        // Assert
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        // Arrange
        long userId = 1;
        User expectedUser = new User(userId, "John", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User actualUser = userService.getUser(userId);

        // Assert
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_ShouldThrowObjectNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void addUser_ShouldReturnAddedUser_WhenValidUserProvided() {
        // Arrange
        User user = new User(null, "John", "john@example.com");
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User addedUser = invocationOnMock.getArgument(0, User.class);
            addedUser.setId(1L);
            return addedUser;
        });

        // Act
        User actualUser = userService.addUser(user);

        // Assert
        User expectedUser = new User(1L, "John", "john@example.com");
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidUserProvided() {
        // Arrange
        User existingUser = new User(1L, "John", "john@example.com");
        User updatedUser = new User(1L, "Updated John", "updated-john@example.com");
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User savingUser = invocationOnMock.getArgument(0, User.class);
            return savingUser;
        });

        // Act
        User actualUser = userService.updateUser(updatedUser);

        // Assert
        assertEquals(updatedUser, actualUser);
        assertEquals("Updated John", actualUser.getName());
        assertEquals("updated-john@example.com", actualUser.getEmail());
        assertEquals(1L, actualUser.getId());
        verify(userRepository, times(1)).findById(existingUser.getId());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void updateUser_ShouldThrowObjectNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        User updatedUser = new User(1L, "Updated John", "updated-john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(updatedUser));
        verify(userRepository, times(1)).findById(updatedUser.getId());
    }

    @Test
    void deleteUser_ShouldCallUserRepositoryDeleteById_WhenValidUserIdProvided() {
        // Arrange
        long userId = 1;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}