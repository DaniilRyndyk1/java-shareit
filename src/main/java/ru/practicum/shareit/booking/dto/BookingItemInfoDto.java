package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class BookingItemInfoDto {
    private Long id;
    private Long bookerId;

    public BookingItemInfoDto(Long id, Long bookerId) {
        this.setId(id);
        this.setBookerId(bookerId);
    }
}
