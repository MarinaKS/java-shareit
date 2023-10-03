package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User updatedUser) {
        User user = userStorage.getUser(updatedUser.getId());
        if (updatedUser.getName() == null) {
            updatedUser.setName(user.getName());
        }
        if (updatedUser.getEmail() == null) {
            updatedUser.setEmail(user.getEmail());
        }
        return userStorage.updateUser(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}
