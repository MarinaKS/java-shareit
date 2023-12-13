package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Test
    public void getItemsTest() throws Exception {
        ItemResponseWithBookingDto itemDto = ItemResponseWithBookingDto.builder()
                .id(1L)
                .name("Велосипед")
                .description("Горный велосипед")
                .available(true)
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        List<ItemResponseWithBookingDto> itemDtoList = Collections.singletonList(itemDto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemService.getItems(anyLong(), anyInt(), anyInt())).thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Велосипед")))
                .andExpect(jsonPath("$[0].description", is("Горный велосипед")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].request").doesNotExist())
                .andExpect(jsonPath("$[0].lastBooking").doesNotExist())
                .andExpect(jsonPath("$[0].nextBooking").doesNotExist())
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
        verify(itemService).getItems(eq(1L), eq(0), eq(10));
    }

    @Test
    public void addItemTest() throws Exception {
        String itemJson = "{\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true}";
        Item itemToAdd = new Item();
        itemToAdd.setName("Item Name");
        itemToAdd.setDescription("Item Description");
        itemToAdd.setIsAvailable(true);
        itemToAdd.setId(1L);
        ItemDto itemDtoAdded = ItemMapper.toItemDto(itemToAdd);
        when(userRepository.findById(123L)).thenReturn(Optional.of(new User()));
        when(itemService.addItem(any(Item.class))).thenReturn(itemToAdd);

        MvcResult mvcResult = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();
        verify(itemService).addItem(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();
        assertEquals("Item Name", capturedItem.getName());
        assertEquals("Item Description", capturedItem.getDescription());
        assertTrue(capturedItem.getIsAvailable());
    }

    @Test
    public void updateItemTest() throws Exception {
        String itemUpdateJson = "{\"name\":\"Updated Item Name\",\"description\":\"Updated Item Description\",\"available\":false}";
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(1L);
        updatedItemDto.setName("Updated Item Name");
        updatedItemDto.setDescription("Updated Item Description");
        updatedItemDto.setAvailable(false);
        Item itemToUpdate = ItemMapper.toItem(updatedItemDto);
        when(itemService.updateItem(any(Item.class))).thenReturn(itemToUpdate);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Item Name"))
                .andExpect(jsonPath("$.description").value("Updated Item Description"))
                .andExpect(jsonPath("$.available").value(false));
        verify(itemService).updateItem(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();
        assertEquals(1L, capturedItem.getId());
        assertEquals("Updated Item Name", capturedItem.getName());
        assertEquals("Updated Item Description", capturedItem.getDescription());
        assertFalse(capturedItem.getIsAvailable());
    }

    @Test
    public void getItemTest() throws Exception {
        long userId = 1L;
        long itemId = 100L;
        ItemResponseWithBookingDto itemResponse = ItemResponseWithBookingDto.builder()
                .id(itemId)
                .name("Пример Вещи")
                .description("Описание Примера Вещи")
                .available(true)
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemService.getItem(itemId, userId)).thenReturn(itemResponse);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponse.getAvailable()));
        verify(itemService).getItem(itemId, userId);
    }

    @Test
    public void searchItemTest() throws Exception {
        String searchText = "велосипед";
        int from = 0;
        int size = 5;
        Item mockedItem1 = Item.builder()
                .id(1L)
                .name("Велосипед")
                .description("Горный велосипед")
                .isAvailable(true)
                .build();

        ItemDto mockedItemDto1 = ItemDto.builder()
                .id(1L)
                .name("Велосипед")
                .description("Горный велосипед")
                .available(true)
                .build();
        List<Item> itemList = Arrays.asList(mockedItem1);
        List<ItemDto> itemDtoList = Arrays.asList(mockedItemDto1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemService.searchItem(searchText, from, size)).thenReturn(itemList);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", searchText)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(mockedItemDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(mockedItemDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(mockedItemDto1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(mockedItemDto1.getAvailable()));
        verify(itemService, times(1)).searchItem(searchText, from, size);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void addCommentTest() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        User author = new User();
        author.setId(userId);
        Comment comment = new Comment();
        comment.setText("Отличный товар!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        CommentDto commentDtoResponse = CommentMapper.toCommentDto(comment);
        when(itemService.addComment(any(Comment.class))).thenReturn(comment);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"text\": \"Отличный товар!\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDtoResponse.getText()))
                .andExpect(jsonPath("$.id").value(commentDtoResponse.getId()));
        verify(itemService).addComment(commentCaptor.capture());
        Comment capturedComment = commentCaptor.getValue();
        assertEquals("Отличный товар!", capturedComment.getText());
        assertEquals(userId, capturedComment.getAuthor().getId());
        assertEquals(itemId, capturedComment.getItem().getId());
    }
}