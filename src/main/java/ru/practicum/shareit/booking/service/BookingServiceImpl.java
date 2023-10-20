package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.UserNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking addBooking(Booking booking) {
        validateBooking(booking);
        fillBookingField(booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Нет бронирования с таким id"));
        if (booking.getStatus() == Status.APPROVED) {
            throw new BookingValidationException("Бронирование уже подтсверждено");
        }
        validateOwner(booking.getItem(), userId);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(long userId, long bookingId) {
        validateUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Нет бронирования с таким id"));
        validateOwnerOrBooker(booking, userId);
        return booking;
    }

    @Override
    public List<Booking> getBookingsByUserIdSorted(long userId, State state) {
        validateUser(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdIsOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdIsAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByBookerIdIsAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBookerIdIsAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(userId, Status.REJECTED);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public List<Booking> getBookingsByItems(long userId, State state) {
        validateUser(userId);
        validateItemExist(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwner(userId);
            case CURRENT:
                return bookingRepository.findAllByOwnerCurrent(userId,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByOwnerPast(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByOwnerFuture(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByOwnerStatusIs(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findAllByOwnerStatusIs(userId, Status.REJECTED);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Booking getBookingByItemId(Long itemId) {
        return bookingRepository.findByItemId(itemId);
    }

    boolean validateBooking(Booking booking) {
        User user = userRepository.findById(booking.getBooker().getId())
                .orElseThrow(() -> new ObjectNotFoundException("нет пользователя с таким id"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ObjectNotFoundException("нет вещи с таким id"));
        if (Objects.equals(item.getOwnerId(), booking.getBooker().getId())) {
            throw new ObjectNotFoundException("Бронировать вещь у самого себя бессмысленно");
        }
        if (!item.getIsAvailable()) {
            throw new ItemUnavailableException("Вещь недоступна для бронирования");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().equals(booking.getEnd()) || !(booking.getStart().isAfter(LocalDateTime.now()))) {
            throw new BookingValidationException("Неверная дата");
        }
        return true;
    }

    boolean validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Нет пользователя с таким id"));
        return true;
    }

    boolean validateItemExist(Long userId) {
        if (itemRepository.findAllByOwnerIdIs(userId).isEmpty()) {
            throw new ObjectNotFoundException("У этого пользователя нет ни одной вещи");
        }
        return true;
    }

    boolean validateItemOwner(Booking booking) {
        if (!Objects.equals(booking.getItem().getOwnerId(), booking.getBooker())) {
            throw new UserNotOwnerException("Пользователь не является хозяином вещи");
        }
        return true;
    }

    private Booking fillBookingField(Booking booking) {
        booking.setItem(itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new IllegalArgumentException()));
        booking.setBooker(userRepository.findById(booking.getBooker().getId())
                .orElseThrow(() -> new IllegalArgumentException()));
        return booking;
    }

    boolean validateOwnerOrBooker(Booking booking, long userId) {
        if (!(booking.getItem().getOwnerId() == userId || booking.getBooker().getId() == userId)) {
            throw new ObjectNotFoundException("Редактировать бронирование может только владелец вещи, либо автор бронирования");
        }
        return true;
    }

    boolean validateOwner(Item item, long userId) {
        if (item.getOwnerId() != userId) {
            throw new ObjectNotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        return true;
    }
}
