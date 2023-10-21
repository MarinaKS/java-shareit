package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByItemId(long itemId);

    List<Booking> findAllByBookerIdIsOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdIsAndAndStatusIsOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByBookerIdIsAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBookerIdIsAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdIsAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (select i.id from Item as i where i.ownerId = ?1) " +
            "order by b.start desc")
    List<Booking> findAllByOwner(long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (select i.id from Item as i where i.ownerId = ?1) " +
            "and b.start < ?2 and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerCurrent(long ownerId, LocalDateTime now, LocalDateTime now2);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (select i.id from Item as i where i.ownerId = ?1) " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerPast(long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (select i.id from Item as i where i.ownerId = ?1) " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerFuture(long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (select i.id from Item as i where i.ownerId = ?1) " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerStatusIs(long ownerId, Status status);

    @Query("select b " +
            "from Booking as b " +
            "where b.start <= ?2 and " +
            "b.item.id = ?1 and " +
            "b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "order by b.start desc")
    List<Booking> findLastBooking(long itemId, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.start >= ?2 and " +
            "b.item.id = ?1 and " +
            "b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "order by b.start")
    List<Booking> findNextBooking(long itemId, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.end < ?3 and " +
            "b.item.id = ?1 and " +
            "b.booker.id = ?2 " +
            "order by b.start")
    List<Booking> findAllByItemIdForComment(long itemId, long userId, LocalDateTime now);
}
