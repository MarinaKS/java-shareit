package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Service
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto, long userId, long itemId, LocalDateTime now) {
        Comment comment = new Comment();
        comment.setId(comment.getId());
        comment.setText(commentDto.getText());
        Item item = new Item();
        item.setId(itemId);
        comment.setItem(item);
        User user = new User();
        user.setId(userId);
        comment.setAuthor(user);
        comment.setCreated(now);
        return comment;
    }
}
