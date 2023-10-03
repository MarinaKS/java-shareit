package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(long userId);

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(long userId, long itemId);

    List<Item> searchItem(long userId, String text);

    Item getItemByItemId(long itemId);
}
