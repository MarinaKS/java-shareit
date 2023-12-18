package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Consts.X_SHARER_USER_ID;


@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserSorted(@RequestHeader(X_SHARER_USER_ID) long userId) {
        return itemRequestClient.getItemRequestsByUserSorted(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsSorted(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getItemRequestsSorted(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}