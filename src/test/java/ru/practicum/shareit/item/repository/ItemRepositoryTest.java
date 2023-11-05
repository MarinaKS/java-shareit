package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAllByOwnerIdIs() {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@example.com");
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

        List<Item> items = itemRepository.findAllByOwnerIdIs(user.getId());

        assertEquals(2, items.size());
    }

    @Test
    public void testFindAllByOwnerIdIsOrderByIdAsc() {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@example.com");
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
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.findAllByOwnerIdIsOrderByIdAsc(user.getId(), pageable);

        assertEquals(2, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item2.getId(), items.get(1).getId());
    }

    @Test
    public void testFindAllByNameOrDescriptionContainingIgnoreCase() {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@example.com");
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
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCase("Item", pageable);

        assertEquals(2, items.size());
    }
}