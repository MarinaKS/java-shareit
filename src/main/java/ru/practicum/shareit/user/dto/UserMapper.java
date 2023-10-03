package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

@Service
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserUpdateDto userUpdateDto(User user) {
        return new UserUpdateDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserUpdateDto userUpdateDto) {
        User user = new User();
        user.setId(userUpdateDto.getId());
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());
        return user;
    }
}
