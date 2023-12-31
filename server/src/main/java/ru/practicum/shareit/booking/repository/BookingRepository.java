package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking b WHERE item.id = :itemId AND b.status = 'APPROVED' AND NOW() BETWEEN b.start AND b.end")
    Booking findCurrentByItem(Long itemId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = :itemId AND EXTRACT(epoch FROM b.start - NOW()) > 0 AND b.status = 'APPROVED' order by EXTRACT(epoch FROM b.start - NOW())")
    List<Booking> findNextBookingsByItem(Long itemId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = :itemId AND EXTRACT(epoch FROM b.start - NOW()) <= 0 ORDER BY EXTRACT(epoch FROM b.start - NOW()) DESC")
    List<Booking> findLastBookingsByItem(Long itemId);

    @Query(value = "select b from Booking b where b.item.id = :itemId and b.booker.id = :userId order by b.end")
    List<Booking> findAllByItemAndUser(Long itemId, Long userId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND NOW() BETWEEN b.start AND b.end ORDER BY b.end")
    List<Booking> findCurrentBookingsByUser(Long userId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND EXTRACT(epoch FROM b.start - NOW()) > 0 AND b.status = 'APPROVED' ORDER BY EXTRACT(epoch FROM b.start - NOW())")
    List<Booking> findNextBookingsByUser(Long userId);

    @Query(value = "SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND EXTRACT(epoch FROM b.start - NOW()) <= 0 ORDER BY EXTRACT(epoch FROM b.start - NOW()) DESC")
    List<Booking> findLastBookingsByUser(Long userId);

    Page<Booking> findAllByItem_Owner_IdOrderByEndDesc(Long id, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndEndLessThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndStartGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndStatusIs(Long ownerId, BookingStatus status, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndEndLessThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndStartGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndStatusIs(Long ownerId, BookingStatus status, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdOrderByEndDesc(Long id, PageRequest pageRequest);

    Booking findFirstByItem_IdAndStartBeforeAndEndAfter(Long itemId, LocalDateTime start, LocalDateTime end);
}
