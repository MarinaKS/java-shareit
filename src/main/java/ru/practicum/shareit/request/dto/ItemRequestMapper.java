package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemRequestMapper {
    private Long id;
    @NotEmpty
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<Item> items;

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemRequest.getItems()) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                itemDtos
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        User user = new User();
        user.setId(itemRequestDto.getRequestId());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}
