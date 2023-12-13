package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Item item;
    private Item item2;
    private Item item3;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private User user1;
    private User user2;

    @BeforeEach
    public void prepare() {
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        user2 = new User();
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
        itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user1);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("item-request-1");
        itemRequest1.setItems(List.of(item));
        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user2);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("item-request-2");
        itemRequest2.setItems(List.of(item2, item3));
        itemRequest2 = itemRequestRepository.save(itemRequest2);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());

        assertEquals(1, requests.size());
        assertEquals(itemRequest1, requests.get(0));
    }

    @Test
    void getItemRequestsSorted() {
        List<ItemRequest> requests = itemRequestRepository.getItemRequestsSorted(user2.getId(), PageRequest.of(0, 20));

        assertEquals(1, requests.size());
        assertEquals(itemRequest1, requests.get(0));
    }
}