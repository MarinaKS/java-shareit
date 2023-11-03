package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testAddBooking() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setIsAvailable(true);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStatus(Status.WAITING);
        LocalDateTime start = LocalDateTime.now().plusMinutes(3);
        LocalDateTime end = start.plusHours(2);
        booking.setStart(start);
        booking.setEnd(end);

        Booking addedBooking = bookingService.addBooking(booking);

        assertNotNull(addedBooking);
        assertNotNull(addedBooking.getId());
    }

    @Test
    public void testApproveStatus() {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@example.com");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setIsAvailable(true);
        item.setOwnerId(user.getId());
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        booking.setStart(start);
        booking.setEnd(end);
        booking = bookingRepository.save(booking);

        Booking updatedBooking = bookingService.approveStatus(user.getId(), booking.getId(), true);

        assertNotNull(updatedBooking);
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    public void testGetBooking() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setIsAvailable(true);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStatus(Status.WAITING);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        booking.setStart(start);
        booking.setEnd(end);
        booking = bookingRepository.save(booking);

        Booking retrievedBooking = bookingService.getBooking(user1.getId(), booking.getId());

        assertNotNull(retrievedBooking);
        assertEquals(user1.getId(), retrievedBooking.getItem().getOwnerId());
    }

    @Test
    public void testGetBookingsByUserIdSorted() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setIsAvailable(true);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(user2);
        booking1.setStatus(Status.WAITING);
        LocalDateTime start1 = LocalDateTime.now();
        LocalDateTime end1 = start1.plusHours(2);
        booking1.setStart(start1);
        booking1.setEnd(end1);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(user2);
        booking2.setStatus(Status.WAITING);
        LocalDateTime start2 = LocalDateTime.now().plusDays(1);
        LocalDateTime end2 = start2.plusHours(2);
        booking2.setStart(start2);
        booking2.setEnd(end2);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingService.getBookingsByUserIdSorted(user2.getId(), State.WAITING, 0, 10);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    public void testGetBookingsByItems() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setIsAvailable(true);
        item1.setOwnerId(user1.getId());
        itemRepository.save(item1);
        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setIsAvailable(true);
        item2.setOwnerId(user1.getId());
        itemRepository.save(item2);
        Booking booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setStatus(Status.WAITING);
        LocalDateTime start1 = LocalDateTime.now();
        LocalDateTime end1 = start1.plusHours(2);
        booking1.setStart(start1);
        booking1.setEnd(end1);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(user1);
        booking2.setStatus(Status.APPROVED);
        LocalDateTime start2 = LocalDateTime.now();
        LocalDateTime end2 = start2.plusHours(2);
        booking2.setStart(start2);
        booking2.setEnd(end2);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingService.getBookingsByItems(user1.getId(), State.ALL, 0, 10);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    public void testGetBookingByItemId() {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@example.com");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailable(true);
        item.setOwnerId(user.getId());
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        booking.setStart(start);
        booking.setEnd(end);
        bookingRepository.save(booking);

        Booking retrievedBooking = bookingService.getBookingByItemId(item.getId());

        assertNotNull(retrievedBooking);
        assertEquals(item.getId(), retrievedBooking.getItem().getId());
        assertEquals(user.getId(), retrievedBooking.getBooker().getId());
        assertEquals(Status.WAITING, retrievedBooking.getStatus());
        assertEquals(start, retrievedBooking.getStart());
        assertEquals(end, retrievedBooking.getEnd());
    }
}