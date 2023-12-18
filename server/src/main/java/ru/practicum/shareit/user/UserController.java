package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> users = userService.getUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        log.info("Получен список всех пользователей");
        return userDtos;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        User user = userService.getUser(userId);
        log.info(String.format("Пользователь id %d найден", userId));
        return UserMapper.toUserDto(user);
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        User addedUser = userService.addUser(UserMapper.toUser(userDto));
        log.info(String.format("создан пользователь: id %d", addedUser.getId()));
        UserDto addedUserDto = UserMapper.toUserDto(addedUser);
        return addedUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserUpdateDto userUpdateDto, @PathVariable long userId) {
        userUpdateDto.setId(userId);
        User updatedUser = userService.updateUser(UserMapper.toUser(userUpdateDto));
        log.info("Пользователь id " + updatedUser.getId() + " обновлён");
        UserDto updatedUserDto = UserMapper.toUserDto(updatedUser);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        log.info(String.format("Пользователь с id=%d удален", userId));
    }
}
