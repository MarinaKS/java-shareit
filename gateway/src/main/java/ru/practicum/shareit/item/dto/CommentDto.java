package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;


@Data
@Builder
public class CommentDto {
    private Long id;
    @NotEmpty
    private String text;
    Long itemId;
    Long authorId;
    String authorName;
    LocalDateTime created;
}
