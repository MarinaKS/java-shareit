package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {
    private Long id;
    private String name;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен иметь формат email@domain.net")
    private String email;
}
