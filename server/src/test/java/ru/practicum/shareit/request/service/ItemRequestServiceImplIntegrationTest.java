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
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("Request Description");
        ItemRequest addedRequest = itemRequestService.addRequest(itemRequest);
        Long requestId = addedRequest.getId();

        assertNotNull(requestId);
        assertTrue(itemRequestRepository.existsById(requestId));
    }

    @Test
    public void testGetItemRequestsByUserSorted() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        ItemRequest request1 = new ItemRequest();
        request1.setRequestor(user);
        request1.setDescription("Request 1 Description");
        itemRequestRepository.save(request1);
        ItemRequest request2 = new ItemRequest();
        request2.setRequestor(user);
        request2.setDescription("Request 2 Description");
        itemRequestRepository.save(request2);
        Long userId = user.getId();
        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUserSorted(userId);

        assertFalse(itemRequests.isEmpty());
        assertEquals(2, itemRequests.size());
    }

    @Test
    public void testGetItemRequestsSorted() {
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

        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsSorted(anotherUserId, 0, 10);

        assertFalse(itemRequests.isEmpty());
        assertEquals(2, itemRequests.size());
    }

    @Test
    public void testGetItemRequestById() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        userRepository.save(user);
        ItemRequest request = new ItemRequest();
        request.setRequestor(user);
        request.setDescription("Request Description");
        itemRequestRepository.save(request);
        Long userId = user.getId();
        Long requestId = request.getId();

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(userId, requestId);

        assertNotNull(itemRequestDto);
    }
}