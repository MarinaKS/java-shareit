package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NonNull
    @NotEmpty
    private String name;
    @NonNull
    @NotEmpty
    private String description;
    @NonNull
    private Boolean available;
    private Long request;
}
