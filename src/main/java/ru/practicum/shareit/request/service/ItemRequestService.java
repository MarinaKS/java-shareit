package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequest itemRequest);

    List<ItemRequestDto> getItemRequestsByUserSorted(long userId);

    List<ItemRequestDto> getItemRequestsSorted(long userId, int from, int size);

    ItemRequestDto getItemRequestById(long userId, long requestId);
}
