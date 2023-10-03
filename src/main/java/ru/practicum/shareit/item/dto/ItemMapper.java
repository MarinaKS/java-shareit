package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@Service
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        if (!(itemDto.getRequest() == null)) {
            //заглушка
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequest());
            item.setRequest(itemRequest);
        }
        return item;
    }

    public static Item toItem(ItemUpdatedDto itemUpdatedDto) {
        Item item = new Item();
        item.setId(itemUpdatedDto.getId());
        item.setName(itemUpdatedDto.getName());
        item.setDescription(itemUpdatedDto.getDescription());
        item.setIsAvailable(itemUpdatedDto.getAvailable());
        if (!(itemUpdatedDto.getRequest() == null)) {
            //заглушка
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemUpdatedDto.getRequest());
            item.setRequest(itemRequest);
        }
        return item;
    }
}
