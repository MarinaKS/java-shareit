package ru.practicum.shareit.booking;

import javax.validation.Valid;

@Valid
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
