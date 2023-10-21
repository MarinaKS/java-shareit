package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;


@Data
@Builder
public class CommentDto {
    private Long id;
    @NonNull
    @NotEmpty
    private String text;
    Long itemId;
    Long authorId;
    String authorName;
    LocalDateTime created;
}
