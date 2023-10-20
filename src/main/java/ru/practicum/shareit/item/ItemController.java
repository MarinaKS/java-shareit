package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemResponseWithBookingDto> getItems(@RequestHeader(X_SHARER_USER_ID) long userId) {
        validateUserIdExist(userId);
        List<ItemResponseWithBookingDto> itemDtos = itemService.getItems(userId);
        log.info("Получен список всех вещей польхователя %s", userId);
        return itemDtos;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid ItemDto itemDto) {
        validateUserIdExist(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        ItemDto itemDtoAdded = ItemMapper.toItemDto(itemService.addItem(item));
        log.info(String.format("Вещь \"%s\" добавлена, с id = \"%s\"", itemDtoAdded.getName(), itemDtoAdded.getId()));
        return itemDtoAdded;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid ItemUpdatedDto itemUpdatedDto, @PathVariable long itemId) {
        itemUpdatedDto.setId(itemId);
        Item item = ItemMapper.toItem(itemUpdatedDto);
        item.setOwnerId(userId);
        ItemDto itemDtoAdded = ItemMapper.toItemDto(itemService.updateItem(item));
        log.info(String.format("Вещь \"%s\" обновлена, с id = \"%s\"", itemDtoAdded.getName(), itemDtoAdded.getId()));
        return itemDtoAdded;
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBookingDto getItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        validateUserIdExist(userId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestParam("text") String text) {
        validateUserIdExist(userId);
        List<Item> items = itemService.searchItem(text);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid CommentDto commentDto,
                                 @PathVariable @Valid long itemId) {
        Comment comment = CommentMapper.toComment(commentDto, userId, itemId, LocalDateTime.now());
        Comment commentAdded = itemService.addComment(comment);
        return CommentMapper.toCommentDto(commentAdded);
    }

    boolean validateUserIdExist(long userId) {
        for (User user : userService.getUsers()) {
            if (user.getId() == userId) {
                return true;
            }
        }
        throw new ObjectNotFoundException("Такого пользователя не добавлено");
    }
}
