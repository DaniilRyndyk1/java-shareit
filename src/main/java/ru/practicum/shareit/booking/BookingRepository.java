package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByEndDesc(Long id);
    List<Booking> findAllByItem_Owner_IdOrderByEndDesc(Long id);
    //List<Booking> findAllByStartLessThenCurrentAndEndGreaterThenCurrentAndItem_IdEqualId(LocalDateTime current, Long id);
    @Query("SELECT b FROM Booking b WHERE cast(:current as timestamp) between start_date AND end_date AND item_id = :itemId")
    List<Booking> findAllByItemAndCrossDate(String current, Long itemId);
}
