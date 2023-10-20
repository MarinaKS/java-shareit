package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findByOwnerId(long ownerId);

    List<Item> findAllByOwnerIdIs(Long ownerId);

    List<Item> findAllByOwnerIdIsOrderByIdAsc(Long ownerId);

    @Query("select item " +
            "from Item as item " +
            "where (upper(item.description) like concat('%', upper(?1), '%') " +
            "or upper(item.name) like concat('%', upper(?1), '%')) " +
            "and item.isAvailable = true " +
            "order by item.id asc")
    List<Item> findAllByNameOrDescriptionContainingIgnoreCase(String text);
}
