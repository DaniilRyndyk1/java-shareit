package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByEndDesc(Long id);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndEndLessThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<Booking> findAllByItem_Owner_IdAndStartGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<Booking> findAllByItem_Owner_IdAndStatusIs(Long ownerId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndEndLessThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<Booking> findAllByBooker_IdAndStartGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<Booking> findAllByBooker_IdAndStatusIs(Long ownerId, BookingStatus status);

    List<Booking> findAllByBooker_IdOrderByEndDesc(Long id);
    @Query(value = "SELECT b FROM Booking b WHERE item.id = :itemId AND status <> 'REJECTED' AND NOW() BETWEEN b.start AND b.end")
    Booking findCurrentByItem(Long itemId);

    @Query(value = "SELECT b FROM Booking b WHERE b.id IN (SELECT b2.id FROM Booking b2 WHERE b2.item.id = :itemId AND EXTRACT(epoch FROM b2.start - NOW()) > 0 AND b2.status <> 'REJECTED' order by EXTRACT(epoch FROM b2.start - NOW()))")
    List<Booking> findNextByItem(Long itemId);

    @Query(value = "SELECT b FROM Booking b WHERE b.id IN (SELECT b2.id FROM Booking b2 WHERE b2.item.id = :itemId AND EXTRACT(epoch FROM b2.start - NOW()) <= 0 ORDER BY EXTRACT(epoch FROM b2.start - NOW()) desc)")
    List<Booking> findLastByItem(Long itemId);
    @Query(value = "select b from Booking b where b.item.id = :itemId and b.booker.id = :userId order by b.end")
    List<Booking> findAllByItemAndUser(Long itemId, Long userId);

    @Query(value = "select b from Booking b where b.booker.id = :userId order by b.end")
    List<Booking> findCurrentsByUser(Long userId);

    @Query(value = "SELECT b FROM Booking b WHERE b.id IN (SELECT b2.id FROM Booking b2 WHERE b2.item.owner.id = :userId AND EXTRACT(epoch FROM b2.start - NOW()) > 0 AND b2.status <> 'REJECTED' order by EXTRACT(epoch FROM b2.start - NOW()))")
    List<Booking> findNextsByUser(Long userId);

    @Query(value = "SELECT b FROM Booking b WHERE b.id IN (SELECT b2.id FROM Booking b2 WHERE b2.item.owner.id = :userId AND EXTRACT(epoch FROM b2.start - NOW()) <= 0 ORDER BY EXTRACT(epoch FROM b2.start - NOW()) desc)")
    List<Booking> findLastsByUser(Long userId);

    Booking findFirstByItem_IdAndStartBeforeAndEndAfter(Long itemId, LocalDateTime start, LocalDateTime end);
}
