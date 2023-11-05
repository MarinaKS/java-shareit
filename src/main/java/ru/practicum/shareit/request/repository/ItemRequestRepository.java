package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long requestorId);

    @Query("select itemRequest  " +
            "from ItemRequest as itemRequest " +
            "where itemRequest.requestor.id != ?1 " +
            "order by itemRequest.created desc")
    List<ItemRequest> getItemRequestsSorted(long userId, Pageable pageable);
}
