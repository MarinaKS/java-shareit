package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddRequest() {
        // Создаем пользователя
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);

        // Создаем запрос на предмет с описанием
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("Request Description");

        // Вызываем метод addRequest
        ItemRequest addedRequest = itemRequestService.addRequest(itemRequest);

        // Получаем ID добавленного запроса
        Long requestId = addedRequest.getId();

        // Проверяем, что запрос был успешно добавлен
        assertNotNull(requestId);

        // Проверяем, что запрос с таким ID существует в базе данных
        assertTrue(itemRequestRepository.existsById(requestId));
    }

    @Test
    public void testGetItemRequestsByUserSorted() {
        // Создаем пользователя
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);

        // Создаем запросы на предмет для пользователя с описанием
        ItemRequest request1 = new ItemRequest();
        request1.setRequestor(user);
        request1.setDescription("Request 1 Description");
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setRequestor(user);
        request2.setDescription("Request 2 Description");
        itemRequestRepository.save(request2);

        // Получаем ID пользователя
        Long userId = user.getId();

        // Вызываем метод getItemRequestsByUserSorted
        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUserSorted(userId);

        // Проверяем, что список запросов не пустой
        assertFalse(itemRequests.isEmpty());
        // Проверяем, что список запросов содержит ожидаемое количество запросов
        assertEquals(2, itemRequests.size());
    }

    @Test
    public void testGetItemRequestsSorted() {
        // Создаем пользователя
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        User anotherUser = new User();
        anotherUser.setName("Userqq");
        anotherUser.setEmail("user@exampleqq.com");
        userRepository.save(anotherUser);
        ItemRequest request1 = new ItemRequest();
        request1.setRequestor(user);
        request1.setDescription("Request 1 Description");
        request1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request1);
        ItemRequest request2 = new ItemRequest();
        request2.setRequestor(user);
        request2.setDescription("Request 2 Description");
        request2.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(request2);
        Long userId = user.getId();
        Long anotherUserId = anotherUser.getId();

        List<ItemRequestDto> itemRequests = itemRequestService.
                getItemRequestsSorted(anotherUserId, 0, 10);

        assertFalse(itemRequests.isEmpty());
        assertEquals(2, itemRequests.size());
    }

    @Test
    public void testGetItemRequestById() {
        // Создаем пользователя
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);

        // Создаем запрос на предмет для пользователя с описанием
        ItemRequest request = new ItemRequest();
        request.setRequestor(user);
        request.setDescription("Request Description");
        itemRequestRepository.save(request);

        // Получаем ID пользователя и запроса
        Long userId = user.getId();
        Long requestId = request.getId();

        // Вызываем метод getItemRequestById
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(userId, requestId);

        // Проверяем, что полученный запрос совпадает с ожидаемым запросом
        assertNotNull(itemRequestDto);
    }
}