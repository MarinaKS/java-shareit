package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Test
    public void getUsersTest() throws Exception {
        User user1 = new User(1L, "Иван Иванов", "ivanov@example.com");
        User user2 = new User(2L, "Петр Петров", "petrov@example.com");
        List<User> users = Arrays.asList(user1, user2);
        UserDto userDto1 = UserMapper.toUserDto(user1);
        UserDto userDto2 = UserMapper.toUserDto(user2);
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].email").value("ivanov@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Петр Петров"))
                .andExpect(jsonPath("$[1].email").value("petrov@example.com"));
    }

    @Test
    public void getUserTest() throws Exception {
        long userId = 1L;
        User mockUser = new User(userId, "Иван Иванов", "ivanov@example.com");
        UserDto mockUserDto = UserMapper.toUserDto(mockUser);
        when(userService.getUser(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(mockUser.getName()))
                .andExpect(jsonPath("$.email").value(mockUser.getEmail()));
    }

    @Test
    public void addUserTest() throws Exception {
        String userJson = "{" +
                "\"name\": \"Иван Иванов\"," +
                "\"email\": \"ivan.ivanov@example.com\"" +
                "}";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Иван Иванов");
        mockUser.setEmail("ivan.ivanov@example.com");
        when(userService.addUser(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan.ivanov@example.com"));
        verify(userService).addUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getId());
        assertEquals("Иван Иванов", capturedUser.getName());
        assertEquals("ivan.ivanov@example.com", capturedUser.getEmail());
    }

    @Test
    public void updateUserTest() throws Exception {
        long userId = 1L;
        String userUpdateJson = "{" +
                "\"name\": \"Иван Иванов\"," +
                "\"email\": \"update.ivanov@example.com\"" +
                "}";

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Иван Иванов");
        updatedUser.setEmail("update.ivanov@example.com");
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("update.ivanov@example.com"));
        verify(userService).updateUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(userId, capturedUser.getId());
        assertEquals("Иван Иванов", capturedUser.getName());
        assertEquals("update.ivanov@example.com", capturedUser.getEmail());
    }

    @Test
    public void deleteUserTest() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userIdCaptor.capture());
        assertEquals(userId, userIdCaptor.getValue());
    }
}