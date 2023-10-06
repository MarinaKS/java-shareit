package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storarge.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public List<Item> getItems(long userId) {
        return itemStorage.getItems(userId);
    }

    @Override
    public Item addItem(Item item) {
        return itemStorage.addItem(item);
    }

    @Override
    public Item updateItem(Item updatedItem) {
        Item item = itemStorage.getItem(updatedItem.getOwnerId(), updatedItem.getId());
        if (updatedItem.getIsAvailable() == null) {
            updatedItem.setIsAvailable(item.getIsAvailable());
        }
        if (updatedItem.getDescription() == null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (updatedItem.getName() == null) {
            updatedItem.setName(item.getName());
        }
        return itemStorage.updateItem(updatedItem);
    }

    @Override
    public Item getItem(long userId, long itemId) {
        return itemStorage.getItem(userId, itemId);
    }

    @Override
    public List<Item> searchItem(long userId, String text) {
        return itemStorage.searchItem(userId, text);
    }

    @Override
    public Item getItemByItemId(long itemId) {
        return itemStorage.getItemByItemId(itemId);
    }
}
