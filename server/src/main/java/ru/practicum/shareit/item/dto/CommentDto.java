package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    Long itemId;
    Long authorId;
    String authorName;
    LocalDateTime created;
}
