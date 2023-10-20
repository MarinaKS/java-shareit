package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemResponseWithBookingDto> getItems(long userId);

    Item addItem(Item item);

    Item updateItem(Item item);

    ItemResponseWithBookingDto getItem(long itemId, long userId);

    List<Item> searchItem(String text);

    Comment addComment(Comment comment);
}
