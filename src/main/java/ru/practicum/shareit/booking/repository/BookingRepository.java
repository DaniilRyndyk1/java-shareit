package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<BookingDto> findAllByItem_Owner_IdOrderByEndDesc(Long id);

    List<BookingDto> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<BookingDto> findAllByItem_Owner_IdAndEndLessThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<BookingDto> findAllByItem_Owner_IdAndStartGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<BookingDto> findAllByItem_Owner_IdAndStatusIs(Long ownerId, BookingStatus status);

    List<BookingDto> findAllByBooker_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<BookingDto> findAllByBooker_IdAndEndLessThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<BookingDto> findAllByBooker_IdAndStartGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime date);

    List<BookingDto> findAllByBooker_IdAndStatusIs(Long ownerId, BookingStatus status);

    List<BookingDto> findAllByBooker_IdOrderByEndDesc(Long id);

    Booking findFirstByItem_IdAndStartBeforeAndEndAfter(Long itemId, LocalDateTime start, LocalDateTime end);
}
