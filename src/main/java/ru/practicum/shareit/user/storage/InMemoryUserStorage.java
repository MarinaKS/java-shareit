package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exceptions.ObjctAlreadyExistException;
import ru.practicum.shareit.Exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> usersMap = new HashMap<>();
    private Long idCount = 0L;


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User getUser(long userId) {
        return usersMap.get(userId);
    }

    @Override
    public User addUser(User user) {
        validateEmailExist(user);
        idCount++;
        user.setId(idCount);
        usersMap.put(user.getId(), user);
        return usersMap.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        if (!usersMap.containsKey(user.getId())) {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        validateEmailExist(user);
        usersMap.put(user.getId(), user);
        return usersMap.get(user.getId());
    }

    @Override
    public void deleteUser(long userId) {
        usersMap.remove(userId);
    }

    boolean validateEmailExist(User user) {
        for (User userInMap : usersMap.values()) {
            if (user.getEmail().equals(userInMap.getEmail()) && userInMap.getId() != user.getId()) {
                throw new ObjctAlreadyExistException("Пользователь с таким email уже добавлен");
            }
        }
        return true;
    }
}
