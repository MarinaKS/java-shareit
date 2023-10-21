package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, long userId) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        Item item = new Item();
        item.setId(bookingDto.getItemId());
        booking.setItem(item);
        User user = new User();
        user.setId(userId);
        booking.setBooker(user);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getStatus()
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemBookingDto(booking.getItem()),
                UserMapper.toUserBookingDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingForItemResponseDto toBookingForItemResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingForItemResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }
}
