package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemResponseWithBookingDto> getItems(long userId, int from, int size);

    Item addItem(Item item);

    Item updateItem(Item item);

    ItemResponseWithBookingDto getItem(long itemId, long userId);

    List<Item> searchItem(String text, int from, int size);

    Comment addComment(Comment comment);
}
