package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
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
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testGetItems() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setIsAvailable(true);
        item1.setOwnerId(user.getId());
        itemRepository.save(item1);
        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setIsAvailable(true);
        item2.setOwnerId(user.getId());
        itemRepository.save(item2);

        List<ItemResponseWithBookingDto> itemDtos = itemService.getItems(user.getId(), 0, 10);

        assertNotNull(itemDtos);
        assertEquals(2, itemDtos.size());
    }

    @Test
    public void testAddItem() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Item item = new Item();
        item.setName("NewItem");
        item.setDescription("New Description");
        item.setIsAvailable(true);
        item.setOwnerId(user.getId());

        Item addedItem = itemService.addItem(item);

        assertNotNull(addedItem);
        assertNotNull(addedItem.getId());
    }

    @Test
    public void testUpdateItem() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailable(true);
        item.setOwnerId(user.getId());
        itemRepository.save(item);
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("UpdatedItem");
        updatedItem.setIsAvailable(true);
        updatedItem.setOwnerId(user.getId());

        Item resultItem = itemService.updateItem(updatedItem);

        assertNotNull(resultItem);
        assertEquals("UpdatedItem", resultItem.getName());
    }

    @Test
    public void testGetItem() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Item item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailable(true);
        item.setOwnerId(user.getId());
        itemRepository.save(item);

        ItemResponseWithBookingDto itemDto = itemService.getItem(item.getId(), user.getId());

        assertNotNull(itemDto);
        assertNotNull(itemDto.getName());
    }

    @Test
    public void testSearchItem() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setIsAvailable(true);
        item1.setOwnerId(user.getId());
        itemRepository.save(item1);
        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setIsAvailable(true);
        item2.setOwnerId(user.getId());
        itemRepository.save(item2);

        List<Item> itemDtos = itemService.searchItem("Item", 0, 10);

        assertNotNull(itemDtos);
        assertEquals(2, itemDtos.size());
    }

    @Test
    public void testAddComment() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        User userBooker = new User();
        userBooker.setName("Userqq");
        userBooker.setEmail("user@exampleqq.com");
        userRepository.save(userBooker);
        Item item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setIsAvailable(true);
        item.setOwnerId(user.getId());
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(userBooker);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().minusHours(2));
        booking.setStatus(Status.CANCELED);
        bookingRepository.save(booking);
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText("Test Comment");
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        Comment newComment = new Comment();
        newComment.setItem(item);
        newComment.setAuthor(userBooker);
        newComment.setText("New Comment");
        newComment.setCreated(LocalDateTime.now());

        Comment addedComment = itemService.addComment(newComment);

        assertNotNull(addedComment);
    }
}