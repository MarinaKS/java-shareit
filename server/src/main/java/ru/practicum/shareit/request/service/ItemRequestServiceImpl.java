package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.pageable.OffsetLimitPageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequest addRequest(ItemRequest itemRequest) {
        validateUserIdExist(itemRequest.getRequestor().getId());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUserSorted(long userId) {
        validateUserIdExist(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getItemRequestsSorted(long userId, int from, int size) {
        validateUserIdExist(userId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .getItemRequestsSorted(userId, new OffsetLimitPageable(from, size));
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        validateUserIdExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Такого запроса не добавлено"));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private boolean validateUserIdExist(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Такого пользователя не добавлено"));
        return true;
    }
}
