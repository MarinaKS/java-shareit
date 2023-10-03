package ru.practicum.shareit.item.storarge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private Map<Long, Map<Long, Item>> itemsMap = new HashMap<>();
    private Long idCount = 0L;

    @Override
    public List<Item> getItems(long userId) {
        return new ArrayList<>(itemsMap.get(userId).values());
    }

    @Override
    public Item addItem(Item item) {
        idCount++;
        item.setId(idCount);
        if (itemsMap.get(item.getOwnerId()) == null) {
            itemsMap.put(item.getOwnerId(), new HashMap<>());
        }
        itemsMap.get(item.getOwnerId()).put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (!itemsMap.get(item.getOwnerId()).containsKey(item.getId())) {
            throw new ObjectNotFoundException("Такой вещи не добавлено");
        }
        itemsMap.get(item.getOwnerId()).put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(long userId, long itemId) {
        if (itemsMap.get(userId) == null || itemsMap.get(userId).get(itemId) == null) {
            throw new ObjectNotFoundException("У этого пользователя нет такой вещи или такого пользователя не существует");
        }
        return itemsMap.get(userId).get(itemId);
    }

    @Override
    public List<Item> searchItem(long userId, String text) {
        List<Item> foundItem = new ArrayList<>();
        if (text.isEmpty()) {
            return foundItem;
        }
        for (Map<Long, Item> items : itemsMap.values()) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getIsAvailable() == true) {
                    foundItem.add(item);
                }
            }
        }
        return foundItem;
    }

    @Override
    public Item getItemByItemId(long itemId) {
        for (Map<Long, Item> items : itemsMap.values()) {
            if (!(items.get(itemId) == null)) {
                return items.get(itemId);
            }
        }
        throw new ObjectNotFoundException("Такой вещи не существует");
    }
}
