package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentValidationException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pageable.OffsetLimitPageable;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testGetItems_ShouldReturnResult_WhenOk() {
        // arrange
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Item> items = List.of(testItem(1L, userId), testItem(2L, userId));
        when(itemRepository.findAllByOwnerIdIsOrderByIdAsc(userId, new OffsetLimitPageable(from, size))).thenReturn(items);
        when(bookingRepository.findLastBooking(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(new ArrayList<>());
        when(bookingRepository.findNextBooking(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemIds(anyList())).thenReturn(new ArrayList<>());

        // act
        List<ItemResponseWithBookingDto> result = itemService.getItems(userId, from, size);

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testItemDto(1), result.get(0));
        assertEquals(testItemDto(2), result.get(1));

        verify(itemRepository).findAllByOwnerIdIsOrderByIdAsc(userId, new OffsetLimitPageable(from, size));
        verify(bookingRepository, times(2)).findLastBooking(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(2)).findNextBooking(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(commentRepository).findAllByItemIds(anyList());
    }

    private static Item testItem(long id, long userId) {
        Item item1 = new Item();
        item1.setId(id);
        item1.setName("name-" + id);
        item1.setDescription("desc-" + id);
        item1.setIsAvailable(true);
        item1.setOwnerId(userId);
        return item1;
    }

    private static ItemResponseWithBookingDto testItemDto(int i) {
        return ItemResponseWithBookingDto.builder()
                .id((long) i)
                .name("name-" + i)
                .description("desc-" + i)
                .available(true)
                .build();
    }

    @Test
    void testAddItem_ShouldReturnItem_WhenOk() {
        // arrange
        Item item = new Item();
        when(itemRepository.save(item)).thenReturn(item);

        // act
        Item result = itemService.addItem(item);

        // assert
        assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItem_ShouldReturnItem_WhenUserIdValid() {
        // arrange
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);
        item.setIsAvailable(true);
        item.setDescription("Description");
        item.setName("Name");
        User user = new User();
        user.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findAll()).thenReturn(List.of(user));

        // act
        Item result = itemService.updateItem(item);
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);

        // assert
        verify(itemRepository).save(captor.capture());
        Item savedItem = captor.getValue();
        assertEquals(savedItem.getId(), 1L);
        assertEquals(savedItem.getName(), "Name");
        assertNotNull(savedItem);
    }

    @Test
    void testUpdateItem_ShouldThrowObjectNotFoundException_WhenUserDoesNotExist() {
        // arrange
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);
        item.setIsAvailable(true);
        item.setDescription("Description");
        item.setName("Name");
        long userId = 1L;
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(item));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateItem_ShouldThrowObjectNotFoundException_WhenItemDoesNotExist() {
        // arrange
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);
        item.setIsAvailable(true);
        item.setDescription("Description");
        item.setName("Name");
        User user = new User();
        long userId = 1L;
        user.setId(userId);
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(item));
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    public void testGetItem_ReturnsItemResponseWithBookingDto_WhenOk() {
        // Arrange
        long itemId = 1;
        long userId = 2;
        Item item = testItem(itemId, userId);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(7L);
        comment.setItem(item);
        comment.setAuthor(new User());
        comment.setText("ghghgh");
        comments.add(comment);
        Booking booking1 = testBooking(item, 1L);
        Booking booking2 = testBooking(item, 2L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(eq(item.getId()), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(booking1));
        when(bookingRepository.findNextBooking(eq(item.getId()), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(booking2));
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);

        // Act
        ItemResponseWithBookingDto result = itemService.getItem(itemId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(booking1.getId(), result.getLastBooking().getId());
        assertEquals(booking2.getId(), result.getNextBooking().getId());
        assertEquals(comment.getId(), result.getComments().get(0).getId());
        assertEquals(1, result.getComments().size());
    }

    private static Booking testBooking(Item item, long bookingId) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(LocalDateTime.now().plusSeconds(0 + bookingId));
        booking.setEnd(LocalDateTime.now().plusMinutes(0 + bookingId / 2));
        booking.setItem(item);
        User booker = new User();
        booker.setId(5 + bookingId);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        return booking;
    }

    @Test
    public void testGetItem_ShouldThrowObjectNotFoundException_WhenItemDoesNotExist() {
        // Arrange
        long itemId = 1;
        long userId = 2;
        Item item = testItem(itemId, userId);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(7L);
        comment.setItem(item);
        comment.setAuthor(new User());
        comment.setText("ghghgh");
        comments.add(comment);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(itemId, userId));
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    public void testSearchItem_ReturnsListOfItems_WhenOk() {
        // Arrange
        String searchText = "book";
        int from = 0;
        int size = 10;
        List<Item> expectedItems = new ArrayList<>();
        expectedItems.add(new Item());

        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCase(searchText,
                new OffsetLimitPageable(from, size))).thenReturn(expectedItems);

        // Act
        List<Item> result = itemService.searchItem(searchText, from, size);

        // Assert
        assertNotNull(result);
        assertEquals(expectedItems.size(), result.size());
    }

    @Test
    public void testSearchItem_ReturnsEmptyList_WhenTextEmpty() {
        // Arrange
        String searchText = "";
        int from = 0;
        int size = 10;

        // Act
        List<Item> result = itemService.searchItem(searchText, from, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_ValidBooking_ReturnsComment() {
        // Arrange
        long itemId = 1;
        long userId = 2;
        Item item = testItem(itemId, userId);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(7L);
        comment.setItem(item);
        User author = new User();
        author.setId(userId);
        comment.setAuthor(author);
        comment.setText("ghghgh");
        comments.add(comment);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        when(bookingRepository.findAllByItemIdForComment(eq(item.getId()), eq(author.getId()),
                any(LocalDateTime.class))).thenReturn(bookings);
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));

        itemService.addComment(comment);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(1)).save(captor.capture());
        Comment savedComment = captor.getValue();
        assertEquals("ghghgh", savedComment.getText());
    }

    @Test
    void addComment_InvalidBooking_ThrowsCommentValidationException() {
        // Arrange
        long itemId = 1;
        long userId = 2;
        Item item = testItem(itemId, userId);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(7L);
        comment.setItem(item);
        User author = new User();
        author.setId(userId);
        comment.setAuthor(author);
        comment.setText("ghghgh");
        comments.add(comment);
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findAllByItemIdForComment(eq(item.getId()), eq(author.getId()),
                any(LocalDateTime.class))).thenReturn(bookings);

        // Act & Assert
        assertThrows(CommentValidationException.class, () -> itemService.addComment(comment));
        verify(commentRepository, never()).save(comment);
    }
}