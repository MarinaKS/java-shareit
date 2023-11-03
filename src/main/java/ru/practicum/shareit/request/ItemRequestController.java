package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.Consts.X_SHARER_USER_ID;


@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.getRequestor().setId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto itemRequestDtoAdded = ItemRequestMapper
                .toItemRequestDto(itemRequestService.addRequest(itemRequest));
        log.info(String.format("Запрос добавлен, с id = \"%s\"", itemRequestDtoAdded.getId()));
        return itemRequestDtoAdded;
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUserSorted(@RequestHeader(X_SHARER_USER_ID) long userId) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequestsByUserSorted(userId);
        log.info("Получен список всех запросов пользователя %s", userId);
        return itemRequestDtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestsSorted(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                      @RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
                                                      @RequestParam(value = "size", required = false) @PositiveOrZero Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = Integer.MAX_VALUE;
            ;
        }
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequestsSorted(userId, from, size);
        log.info("Получен список всех запросов пользователя %s", userId);
        return itemRequestDtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}