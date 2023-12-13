package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pageable.OffsetLimitPageable;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private static Item testItem(long id, long userId) {
        Item item1 = new Item();
        item1.setId(id);
        item1.setName("name-" + id);
        item1.setDescription("desc-" + id);
        item1.setIsAvailable(true);
        item1.setOwnerId(userId);
        return item1;
    }

    @Test
    public void testAddBooking_ShouldSaveBooking_WhenValid() {
        User booker = new User();
        booker.setId(1L);
        User owner = new User();
        owner.setId(2L);
        Item item = testItem(3L, owner.getId());
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStart(LocalDateTime.now().plusHours(1));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.addBooking(booking);

        verify(userRepository, times(2)).findById(booker.getId());
        verify(itemRepository, times(2)).findById(item.getId());
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking savedBooking = captor.getValue();
        assertEquals(booking, result);
        assertEquals(1L, savedBooking.getBooker().getId());
        assertEquals(booker, savedBooking.getBooker());
        assertEquals(3L, savedBooking.getItem().getId());
        assertEquals(item, savedBooking.getItem());
    }

    @Test
    public void testAddBooking_ShouldThrowException_WhenUserDoesNotExist() {
        Booking booking = new Booking();
        User user = new User();
        user.setId(1L);
        booking.setBooker(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.addBooking(booking));
    }

    @Test
    public void testAddBooking_ShouldThrowException_WhenItemDoesNotExist() {
        Booking booking = new Booking();
        User user = new User();
        user.setId(1L);
        booking.setBooker(user);
        Item item = new Item();
        item.setId(2L);
        booking.setItem(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.addBooking(booking));
    }

    @Test
    public void testAddBooking_ShouldThrowException_WhenUserBooksOwnItem() {
        Booking booking = new Booking();
        User user = new User();
        user.setId(1L);
        booking.setBooker(user);
        Item item = new Item();
        item.setId(2L);
        item.setOwnerId(1L);
        booking.setItem(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        String message =
                assertThrows(ObjectNotFoundException.class,
                        () -> bookingService.addBooking(booking)).getMessage();
        System.out.println(message);
    }

    @Test
    public void testAddBooking_ShouldThrowException_WhenItemUnavailable() {
        Booking booking = new Booking();
        User user = new User();
        user.setId(1L);
        booking.setBooker(user);
        Item item = new Item();
        item.setId(2L);
        item.setIsAvailable(false);
        booking.setItem(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ItemUnavailableException.class, () -> bookingService.addBooking(booking));
    }

    @Test
    public void testAddBooking_ShouldThrowException_WhenDatesInvalid() {
        Booking booking = new Booking();
        User user = new User();
        user.setId(1L);
        booking.setBooker(user);
        Item item = new Item();
        item.setId(2L);
        item.setIsAvailable(true);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(3));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(BookingValidationException.class, () -> bookingService.addBooking(booking));
    }

    @Test
    public void testApproveStatus_ShouldUpdateBookingStatus_WhenValid() {
        Booking booking = new Booking();
        Item item = testItem(3L, 1L);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking result = bookingService.approveStatus(1L, 1L, true);

        verify(bookingRepository).findById(1L);
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking savedBooking = captor.getValue();
        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }

    @Test
    public void testApproveStatus_ShouldThrowException_WhenAlreadyApproved() {
        long userId = 1L;
        long bookingId = 2L;
        Booking booking = new Booking();
        Item item = testItem(3L, userId);
        booking.setItem(item);
        booking.setId(bookingId);
        booking.setStatus(Status.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingValidationException.class, () -> bookingService.approveStatus(userId, bookingId, true));
    }

    @Test
    public void testApproveStatus_ShouldThrowException_WhenNotOwner() {
        long userId = 1L;
        User booker = new User();
        booker.setId(userId);
        long bookingId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        Item item = new Item();
        User owner = new User();
        owner.setId(3L);
        item.setOwnerId(owner.getId());
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.approveStatus(userId, bookingId, true));
    }

    @Test
    public void testApproveStatus_ShouldThrowException_WhenBookingNotFound() {
        long userId = 1L;
        long bookingId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        String message =
                assertThrows(ObjectNotFoundException.class,
                        () -> bookingService.approveStatus(userId, bookingId, true)).getMessage();
        System.out.println(message);
    }

    @Test
    public void testGetBooking_ShouldReturnBooking_WhenUserValid() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        Item item = testItem(3L, userId);
        booking.setItem(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(1L, 1L);

        verify(userRepository).findById(1L);
        verify(bookingRepository).findById(1L);
        assertEquals(booking, result);
    }

    @Test
    public void testGetBooking_ShouldThrowException_WhenUserDoesNotExist() {
        long userId = 1L;
        long bookingId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldThrowException_InvalidUser() {
        long userId = -1; // Некорректное значение
        State state = State.ALL;
        int from = 0;
        int size = 10;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingsByUserIdSorted(userId, state, from, size));
        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldReturnBookings_All() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdIsOrderByStartDesc(eq(userId), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByUserIdSorted(userId, State.ALL, 0, 10);

        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdIsOrderByStartDesc(eq(userId), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldReturnBookings_Current() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdIsAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByUserIdSorted(userId, State.CURRENT, 0, 10);

        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdIsAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldReturnBookings_Past() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdIsAndEndIsBeforeOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByUserIdSorted(userId, State.PAST, 0, 10);

        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdIsAndEndIsBeforeOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldReturnBookings_Future() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdIsAndStartIsAfterOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByUserIdSorted(userId, State.FUTURE, 0, 10);

        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdIsAndStartIsAfterOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldReturnBookings_Waiting() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(
                eq(userId), eq(Status.WAITING), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByUserIdSorted(userId, State.WAITING, 0, 10);

        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(
                eq(userId), eq(Status.WAITING), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByUserIdSorted_ShouldReturnBookings_Rejected() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(
                eq(userId), eq(Status.REJECTED), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByUserIdSorted(userId, State.REJECTED, 0, 10);

        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(
                eq(userId), eq(Status.REJECTED), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByItems_InvalidUser() {
        long userId = 10L;
        State state = State.ALL;
        int from = 0;
        int size = 10;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsByItems(userId, state, from, size));
        verify(userRepository).findById(userId);
    }

    @Test
    public void testGetBookingsByItems_NoItemsForUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        State state = State.ALL;
        int from = 0;
        int size = 10;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.emptyList());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsByItems(userId, state, from, size));
        verify(itemRepository).findAllByOwnerIdIs(userId);
    }

    @Test
    public void testGetBookingsByItems_ShouldReturnBookings_All() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingRepository.findAllByOwner(eq(userId), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByItems(userId, State.ALL, 0, 10);

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllByOwnerIdIs(userId);
        verify(bookingRepository).findAllByOwner(eq(userId), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByItems_ShouldReturnBookings_Current() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingRepository.findAllByOwnerCurrent(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByItems(userId, State.CURRENT, 0, 10);

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllByOwnerIdIs(userId);
        verify(bookingRepository).findAllByOwnerCurrent(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByItems_ShouldReturnBookings_Past() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingRepository.findAllByOwnerPast(
                eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByItems(userId, State.PAST, 0, 10);

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllByOwnerIdIs(userId);
        verify(bookingRepository).findAllByOwnerPast(
                eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByItems_ShouldReturnBookings_Future() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingRepository.findAllByOwnerFuture(eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByItems(userId, State.FUTURE, 0, 10);

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllByOwnerIdIs(userId);
        verify(bookingRepository).findAllByOwnerFuture(eq(userId), any(LocalDateTime.class), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByItems_ShouldReturnBookings_Waiting() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingRepository.findAllByOwnerStatusIs(eq(userId), eq(Status.WAITING), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByItems(userId, State.WAITING, 0, 10);

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllByOwnerIdIs(userId);
        verify(bookingRepository).findAllByOwnerStatusIs(eq(userId), eq(Status.WAITING), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingsByItems_ShouldReturnBookings_Rejected() {
        long userId = 1L;
        List<Booking> expectedBookings = Collections.singletonList(new Booking());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdIs(userId)).thenReturn(Collections.singletonList(new Item()));
        when(bookingRepository.findAllByOwnerStatusIs(eq(userId), eq(Status.REJECTED), any(OffsetLimitPageable.class)))
                .thenReturn(expectedBookings);

        List<Booking> result = bookingService.getBookingsByItems(userId, State.REJECTED, 0, 10);

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllByOwnerIdIs(userId);
        verify(bookingRepository).findAllByOwnerStatusIs(eq(userId), eq(Status.REJECTED), any(OffsetLimitPageable.class));
        assertEquals(expectedBookings, result);
    }

    @Test
    public void testGetBookingByItemId_ShouldReturnBooking_WhenOk() {
        Item item = new Item();
        item.setId(1L);
        Booking booking = new Booking();
        booking.setItem(item);

        when(bookingRepository.findByItemId(item.getId())).thenReturn(booking);

        Booking result = bookingService.getBookingByItemId(1L);

        verify(bookingRepository).findByItemId(1L);
        assertEquals(booking, result);
    }
}