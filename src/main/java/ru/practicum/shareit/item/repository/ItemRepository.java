package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT b FROM Booking b WHERE item.id = :itemId AND status <> 'REJECTED' AND NOW() BETWEEN b.start AND b.end")
    Booking findCurrentBookingByItem(Long itemId);

    @Query(value = "SELECT b FROM Booking b WHERE b.id IN (SELECT b2.id FROM Booking b2 WHERE b2.item.id = :itemId AND EXTRACT(epoch FROM b2.start - NOW()) > 0 AND b2.status <> 'REJECTED' order by EXTRACT(epoch FROM b2.start - NOW()))")
    List<Booking> findNextBookingByItem(Long itemId);

    @Query(value = "SELECT b FROM Booking b WHERE b.id IN (SELECT b2.id FROM Booking b2 WHERE b2.item.id = :itemId AND EXTRACT(epoch FROM b2.start - NOW()) <= 0 ORDER BY EXTRACT(epoch FROM b2.start - NOW()) desc)")
    List<Booking> findLastBookingByItem(Long itemId);

    List<Item> findAllByOwner_IdOrderById(Long ownerId);

    @Query(value = "select b from Booking b where b.item.id = :itemId and b.booker.id = :userId order by b.end")
    List<Booking> findBookingsByItemAndUser(Long itemId, Long userId);
}
