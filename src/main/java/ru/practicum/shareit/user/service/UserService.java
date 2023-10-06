package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUser(long userId);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);
}
