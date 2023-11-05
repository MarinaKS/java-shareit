package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestCaptor;

    @Test
    public void addItemRequestTest() throws Exception {
        long userId = 1L;
        String requestJson = "{\"description\": \"Нужен дрель\"}";
        ItemRequest mockedItemRequest = new ItemRequest();
        mockedItemRequest.setId(10L);
        mockedItemRequest.setDescription("Нужен дрель");
        User requestor = new User();
        requestor.setId(userId);
        mockedItemRequest.setRequestor(requestor);
        mockedItemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto itemRequestDtoResponse = ItemRequestMapper.toItemRequestDto(mockedItemRequest);
        when(itemRequestService.addRequest(any(ItemRequest.class))).thenReturn(mockedItemRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()));
        verify(itemRequestService).addRequest(itemRequestCaptor.capture());
        ItemRequest capturedItemRequest = itemRequestCaptor.getValue();
        assertEquals("Нужен дрель", capturedItemRequest.getDescription());
        assertEquals(userId, capturedItemRequest.getRequestor().getId());
    }

    @Test
    public void getItemRequestsByUserSortedTest() throws Exception {
        long userId = 1L;
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(10L)
                .description("Первый запрос")
                .build();
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(20L)
                .description("Второй запрос")
                .build();
        List<ItemRequestDto> itemRequestDtoList = Arrays.asList(itemRequestDto1, itemRequestDto2);
        when(itemRequestService.getItemRequestsByUserSorted(userId)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto1.getDescription()))
                .andExpect(jsonPath("$[1].id").value(itemRequestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(itemRequestDto2.getDescription()));
        verify(itemRequestService).getItemRequestsByUserSorted(userId);
    }

    @Test
    public void getItemRequestsSortedTest() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(10L)
                .description("Запрос 1")
                .build();
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(20L)
                .description("Запрос 2")
                .build();
        List<ItemRequestDto> expectedDtos = Arrays.asList(itemRequestDto1, itemRequestDto2);
        when(itemRequestService.getItemRequestsSorted(userId, from, size)).thenReturn(expectedDtos);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedDtos.size())))
                .andExpect(jsonPath("$[0].id").value(itemRequestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto1.getDescription()))
                .andExpect(jsonPath("$[1].id").value(itemRequestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(itemRequestDto2.getDescription()));
        verify(itemRequestService).getItemRequestsSorted(userId, from, size);
    }

    @Test
    public void getItemRequestByIdTest() throws Exception {
        long userId = 1L;
        long requestId = 123L;
        LocalDateTime created = LocalDateTime.now();
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Название вещи");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(requestId);
        itemRequestDto.setDescription("Пример описания запроса");
        itemRequestDto.setRequestId(requestId);
        itemRequestDto.setCreated(created);
        itemRequestDto.setItems(Collections.singletonList(itemDto));
        when(itemRequestService.getItemRequestById(userId, requestId)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestId").value(itemRequestDto.getRequestId()))
                .andExpect(jsonPath("$.created").exists()) // Проверяем только наличие поля, т.к. точное значение зависит от момента запуска теста
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.items[0].name").value(itemDto.getName()));
        verify(itemRequestService).getItemRequestById(userId, requestId);
    }
}