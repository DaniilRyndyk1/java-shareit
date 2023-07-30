package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStateException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String userHeaderName = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestBody BookingInputDto object, @RequestHeader(userHeaderName) Long userId) {
        return service.create(object, userId).toDto();
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@PathVariable long bookingId, Boolean approved, @RequestHeader(userHeaderName) Long userId) {
        return service.approve(bookingId, userId, approved == null || approved).toDto();
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId, @RequestHeader(userHeaderName) Long userId) {
        return service.get(bookingId, userId).toDto();
    }

    @GetMapping
    public List<BookingDto> getBookings(String state, @RequestHeader(userHeaderName) Long userId) {
        return service.getBookingsByBookerAndState(state == null ? "ALL" : state, userId);
    }

    @GetMapping("owner")
    public List<BookingDto> getItemsBookings(String state, @RequestHeader(userHeaderName) Long userId) {
        return service.getBookingsByOwnerAndState(state == null ? "ALL" : state, userId);
    }

    @ExceptionHandler(UnsupportedStateException.class)
    public ResponseEntity<Map<String, String>> handleException(UnsupportedStateException e) {
        Map<String, String> errorResponse = Map.of(
                "error", e.getMessage(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.toString()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
