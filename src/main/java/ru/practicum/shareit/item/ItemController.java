package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Exceptions.ObjectNotFoundException;
import ru.practicum.shareit.Exceptions.UpdateItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        validateUserIdExist(userId);
        List<Item> items = itemService.getItems(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        log.info("Получен список всех вещей польхователя %s", userId);
        return itemDtos;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        validateUserIdExist(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        ItemDto itemDtoAdded = ItemMapper.toItemDto(itemService.addItem(item));
        log.info(String.format("Вещь \"%s\" добавлена, с id = \"%s\"", itemDtoAdded.getName(), itemDtoAdded.getId()));
        return itemDtoAdded;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemUpdatedDto itemUpdatedDto, @PathVariable long itemId) {
        validateUserIdExist(userId);
        Item itemValidate = itemService.getItem(userId, itemId);
        validateOwner(itemValidate, userId);
        itemUpdatedDto.setId(itemId);
        Item item = ItemMapper.toItem(itemUpdatedDto);
        item.setOwnerId(userId);
        ItemDto itemDtoAdded = ItemMapper.toItemDto(itemService.updateItem(item));
        log.info(String.format("Вещь \"%s\" обновлена, с id = \"%s\"", itemDtoAdded.getName(), itemDtoAdded.getId()));
        return itemDtoAdded;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        validateUserIdExist(userId);
        Item item = itemService.getItemByItemId(itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String text) {
        validateUserIdExist(userId);
        List<Item> items = itemService.searchItem(userId, text);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    boolean validateOwner(Item item, long userId) {
        if (item.getOwnerId() != userId) {
            throw new UpdateItemException("Редактировать вещь может только ее пользователь");
        }
        return true;
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
