package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
class CommentRepositoryTest {


    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;
    private Comment comment3;
    private Comment comment2;
    private Comment comment1;
    private Comment comment4;
    private Comment comment5;
    private Item item2;
    private Item item;
    private Item item3;

    @BeforeEach
    public void prepare() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailable(true);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setIsAvailable(true);
        item2.setOwnerId(user1.getId());
        itemRepository.save(item2);
        item3 = new Item();
        item3.setName("Item3");
        item3.setDescription("Description3");
        item3.setIsAvailable(true);
        item3.setOwnerId(user1.getId());
        itemRepository.save(item3);
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
        comment1 = new Comment();
        comment1.setAuthor(user1);
        comment1.setItem(item);
        comment1.setText("Комментарий пользователя 1 для booking1");
        commentRepository.save(comment1);
        comment2 = new Comment();
        comment2.setAuthor(user2);
        comment2.setItem(item);
        comment2.setText("Комментарий пользователя 2 для booking2");
        commentRepository.save(comment2);
        comment3 = new Comment();
        comment3.setAuthor(user1);
        comment3.setItem(item2);
        comment3.setText("Комментарий пользователя 1 для booking2");
        commentRepository.save(comment3);
        comment4 = new Comment();
        comment4.setAuthor(user2);
        comment4.setItem(item2);
        comment4.setText("Комментарий пользователя 2 для booking2");
        commentRepository.save(comment4);
        comment5 = new Comment();
        comment5.setAuthor(user2);
        comment5.setItem(item3);
        comment5.setText("Комментарий пользователя 2 для booking3");
        commentRepository.save(comment5);
    }

    @Test
    void findByItemId() {
        List<Comment> comments = commentRepository.findByItemId(item.getId());

        assertEquals(2, comments.size());
        assertEquals(comment1, comments.get(0));
        assertEquals(comment2, comments.get(1));
    }

    @Test
    void findAllByItemIds() {
        List<Comment> comments = commentRepository.findAllByItemIds(List.of(item2.getId(), item3.getId()));

        assertEquals(3, comments.size());
        assertEquals(comment3, comments.get(0));
        assertEquals(comment4, comments.get(1));
        assertEquals(comment5, comments.get(2));
    }
}