package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.Consts.X_SHARER_USER_ID;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader(X_SHARER_USER_ID) long userId, @RequestBody @Valid BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto, userId);
        booking.setStatus(Status.WAITING);
        log.info(booking.getStart() + "     " + booking.getEnd());
        return BookingMapper.toBookingResponseDto(bookingService.addBooking(booking));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveStatus(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long bookingId,
                                            @RequestParam("approved") boolean approved) {
        Booking booking = bookingService.approveStatus(userId, bookingId, approved);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long bookingId) {
        Booking booking = bookingService.getBooking(userId, bookingId);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUserIdSorted(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(value = "state", defaultValue = "ALL") @Valid State state,
            @RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false) @PositiveOrZero Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = Integer.MAX_VALUE;
        }
        List<Booking> bookings = bookingService.getBookingsByUserIdSorted(userId, state, from, size);
        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toBookingResponseDto(booking));
        }
        return bookingDtos;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByItems(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @RequestParam(value = "state", defaultValue = "ALL") State state,
            @RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false) @PositiveOrZero Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = Integer.MAX_VALUE;
            ;
        }
        List<Booking> bookings = bookingService.getBookingsByItems(userId, state, from, size);
        List<BookingResponseDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toBookingResponseDto(booking));
        }
        return bookingDtos;
    }
}
