package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

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
        if (!(itemDto.getRequestId() == null)) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
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
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemUpdatedDto.getRequest());
            item.setRequest(itemRequest);
        }
        return item;
    }

    public static ItemBookingDto toItemBookingDto(Item item) {
        return new ItemBookingDto(
                item.getId(),
                item.getName()
        );
    }

    public static ItemResponseWithBookingDto toItemResponseWithBookingDto(Item item,
                                                                          BookingForItemResponseDto lastBooking,
                                                                          BookingForItemResponseDto nextBooking,
                                                                          List<CommentDto> commentDtos
    ) {
        return new ItemResponseWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                commentDtos
        );
    }
}
