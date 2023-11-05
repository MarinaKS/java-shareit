package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testFindByItemId() {
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
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);

        Booking foundBooking = bookingRepository.findByItemId(item.getId());

        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    public void testFindAllByBookerIdIsOrderByStartDesc() {
        User user1 = createUser("user1", "user1@example.com");
        User user2 = createUser("user2", "user2@example.com");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item();
        item.setName("Предмет 1");
        item.setDescription("Описание предмета 1");
        item.setOwnerId(user1.getId());
        item.setIsAvailable(true);
        itemRepository.save(item);
        Booking booking1 = createBooking(user1, item, Status.APPROVED,
                LocalDateTime.now().minusDays(1), LocalDateTime.now());
        Booking booking2 = createBooking(user1, item, Status.APPROVED,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        Booking booking3 = createBooking(user2, item, Status.APPROVED,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findAllByBookerIdIsOrderByStartDesc(user1.getId(), PageRequest.of(0, 10));

        assertEquals(2, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        return user;
    }

    private Booking createBooking(User booker, Item item, Status status, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }

    private Item createItem(User owner) {
        Item item = new Item();
        item.setName("Предмет 1");
        item.setDescription("Описание предмета 1");
        item.setOwnerId(owner.getId());
        item.setIsAvailable(true);
        itemRepository.save(item);
        return item;
    }

    @Test
    public void testFindAllByBookerIdIsAndAndStatusIsOrderByStartDesc() {
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
        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStatus(Status.REJECTED); // Отличается статус
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1).plusHours(1));
        bookingRepository.save(booking2);

        List<Booking> approvedBookings = bookingRepository.findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(user.getId(), Status.APPROVED, PageRequest.of(0, 10));

        assertEquals(1, approvedBookings.size());
        assertEquals(Status.APPROVED, approvedBookings.get(0).getStatus());
    }

    @Test
    public void testFindLastBooking() {
        User user = createUser("user1", "user1@example.com");
        userRepository.save(user);
        Item item = createItem(user);
        Booking booking1 = createBooking(user, item, Status.APPROVED, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        Booking booking2 = createBooking(user, item, Status.APPROVED, LocalDateTime.now().minusDays(2), LocalDateTime.now());
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> lastBookings = bookingRepository.findLastBooking(item.getId(), LocalDateTime.now(), PageRequest.of(0, 1));

        assertEquals(1, lastBookings.size());
        assertEquals(booking1, lastBookings.get(0));
    }

    @Test
    public void testFindNextBooking() {
        User user = createUser("user1", "user1@example.com");
        userRepository.save(user);
        Item item = createItem(user);
        Booking booking1 = createBooking(user, item, Status.APPROVED, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        Booking booking2 = createBooking(user, item, Status.APPROVED, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> nextBookings = bookingRepository.findNextBooking(item.getId(), LocalDateTime.now(), PageRequest.of(0, 1));

        assertEquals(1, nextBookings.size());
        assertEquals(booking2, nextBookings.get(0));
    }

    @Test
    public void testFindAllByItemIdForComment() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        Item item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailable(true);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        Booking booking1 = new Booking();
        booking1.setBooker(user1);
        booking1.setItem(item);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now());
        bookingRepository.save(booking1);
        Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking2);
        Comment comment1 = new Comment();
        comment1.setAuthor(user1);
        comment1.setItem(item);
        comment1.setText("Комментарий пользователя 1 для booking1");
        commentRepository.save(comment1);
        Comment comment2 = new Comment();
        comment2.setAuthor(user2);
        comment2.setItem(item);
        comment2.setText("Комментарий пользователя 2 для booking2");
        commentRepository.save(comment2);

        List<Booking> bookingsForComment = bookingRepository.findAllByItemIdForComment(item.getId(), user1.getId(), LocalDateTime.now());

        assertEquals(1, bookingsForComment.size());
        assertEquals(booking1.getId(), bookingsForComment.get(0).getId());
    }

    @Test
    void testFindAllByBookerIdIsAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner = createUser("owner", "owner@test.com");
        owner = userRepository.save(owner);
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        pastBooking = bookingRepository.save(pastBooking);
        Booking currentBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        currentBooking = bookingRepository.save(currentBooking);
        Booking futureBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.plusHours(1), now.plusHours(2));
        futureBooking = bookingRepository.save(futureBooking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdIsAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                booker.getId(), now, now, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindAllByBookerIdIsAndEndIsBeforeOrderByStartDesc() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner = createUser("owner", "owner@test.com");
        owner = userRepository.save(owner);
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        pastBooking = bookingRepository.save(pastBooking);
        Booking currentBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        currentBooking = bookingRepository.save(currentBooking);
        Booking futureBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.plusHours(1), now.plusHours(2));
        futureBooking = bookingRepository.save(futureBooking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdIsAndEndIsBeforeOrderByStartDesc(
                booker.getId(), now, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindAllByBookerIdIsAndStartIsAfterOrderByStartDesc() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner = createUser("owner", "owner@test.com");
        owner = userRepository.save(owner);
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        pastBooking = bookingRepository.save(pastBooking);
        Booking currentBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        currentBooking = bookingRepository.save(currentBooking);
        Booking futureBooking = createBooking(booker, createItem(owner), Status.APPROVED, now.plusHours(1), now.plusHours(2));
        futureBooking = bookingRepository.save(futureBooking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdIsAndStartIsAfterOrderByStartDesc(
                booker.getId(), now, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindAllByOwner() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner1 = createUser("owner", "owner@test.com");
        owner1 = userRepository.save(owner1);
        User owner2 = createUser("owner2", "owner2@test.com");
        owner2 = userRepository.save(owner2);
        LocalDateTime now = LocalDateTime.now();
        Booking owner1Booking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        owner1Booking = bookingRepository.save(owner1Booking);
        Booking owner2Booking = createBooking(booker, createItem(owner2), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        owner2Booking = bookingRepository.save(owner2Booking);

        List<Booking> bookings = bookingRepository.findAllByOwner(owner1.getId(), PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(owner1Booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindAllByOwnerCurrent() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner1 = createUser("owner", "owner@test.com");
        owner1 = userRepository.save(owner1);
        User owner2 = createUser("owner2", "owner2@test.com");
        owner2 = userRepository.save(owner2);
        LocalDateTime now = LocalDateTime.now();
        Booking owner1PastBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        owner1PastBooking = bookingRepository.save(owner1PastBooking);
        Booking owner1NowBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.plusHours(1));
        owner1NowBooking = bookingRepository.save(owner1NowBooking);
        Booking owner1FutureBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.plusHours(2), now.plusHours(3));
        owner1FutureBooking = bookingRepository.save(owner1FutureBooking);
        Booking owner2Booking = createBooking(booker, createItem(owner2), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        owner2Booking = bookingRepository.save(owner2Booking);

        List<Booking> bookings = bookingRepository.findAllByOwnerCurrent(owner1.getId(), now, now, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(owner1NowBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByOwnerPast() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner1 = createUser("owner", "owner@test.com");
        owner1 = userRepository.save(owner1);
        User owner2 = createUser("owner2", "owner2@test.com");
        owner2 = userRepository.save(owner2);
        LocalDateTime now = LocalDateTime.now();
        Booking owner1PastBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        owner1PastBooking = bookingRepository.save(owner1PastBooking);
        Booking owner1NowBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.plusHours(1));
        owner1NowBooking = bookingRepository.save(owner1NowBooking);
        Booking owner1FutureBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.plusHours(2), now.plusHours(3));
        owner1FutureBooking = bookingRepository.save(owner1FutureBooking);
        Booking owner2Booking = createBooking(booker, createItem(owner2), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        owner2Booking = bookingRepository.save(owner2Booking);


        List<Booking> bookings = bookingRepository.findAllByOwnerPast(owner1.getId(), now, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(owner1PastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByOwnerFuture() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner1 = createUser("owner", "owner@test.com");
        owner1 = userRepository.save(owner1);
        User owner2 = createUser("owner2", "owner2@test.com");
        owner2 = userRepository.save(owner2);
        LocalDateTime now = LocalDateTime.now();
        Booking owner1PastBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.minusHours(1));
        owner1PastBooking = bookingRepository.save(owner1PastBooking);
        Booking owner1NowBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.plusHours(1));
        owner1NowBooking = bookingRepository.save(owner1NowBooking);
        Booking owner1FutureBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.plusHours(2), now.plusHours(3));
        owner1FutureBooking = bookingRepository.save(owner1FutureBooking);
        Booking owner2Booking = createBooking(booker, createItem(owner2), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        owner2Booking = bookingRepository.save(owner2Booking);

        List<Booking> bookings = bookingRepository.findAllByOwnerFuture(owner1.getId(), now, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(owner1FutureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByOwnerStatusIs() {
        User booker = createUser("booker", "booker@test.com");
        booker = userRepository.save(booker);
        User owner1 = createUser("owner", "owner@test.com");
        owner1 = userRepository.save(owner1);
        User owner2 = createUser("owner2", "owner2@test.com");
        owner2 = userRepository.save(owner2);
        LocalDateTime now = LocalDateTime.now();
        Booking owner1CancelledBooking = createBooking(booker, createItem(owner1), Status.CANCELED, now.minusHours(2), now.minusHours(1));
        owner1CancelledBooking = bookingRepository.save(owner1CancelledBooking);
        Booking owner1ApprovedBooking = createBooking(booker, createItem(owner1), Status.APPROVED, now.minusHours(2), now.plusHours(1));
        owner1ApprovedBooking = bookingRepository.save(owner1ApprovedBooking);
        Booking owner1RejectedBooking = createBooking(booker, createItem(owner1), Status.REJECTED, now.plusHours(2), now.plusHours(3));
        owner1RejectedBooking = bookingRepository.save(owner1RejectedBooking);
        Booking owner2Booking = createBooking(booker, createItem(owner2), Status.APPROVED, now.minusMinutes(30), now.plusMinutes(30));
        owner2Booking = bookingRepository.save(owner2Booking);

        List<Booking> bookings = bookingRepository.findAllByOwnerStatusIs(owner1.getId(), Status.REJECTED, PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(owner1RejectedBooking.getId(), bookings.get(0).getId());
    }
}