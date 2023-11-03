package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking);

    Booking approveStatus(long userId, long bookingId, boolean approved);

    Booking getBooking(long userId, long bookingId);

    List<Booking> getBookingsByUserIdSorted(long userId, State state, int from, int size);

    List<Booking> getBookingsByItems(long userId, State state, int from, int size);

    Booking getBookingByItemId(Long id);
}
