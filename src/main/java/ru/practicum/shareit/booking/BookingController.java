package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingInputDto object, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.create(object, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@PathVariable long bookingId, @RequestParam(defaultValue = "true") Boolean approved, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.approve(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "ALL") State state, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getBookingsByBookerAndState(state, userId, from, size);
    }

    @GetMapping("owner")
    public List<BookingDto> getItemsBookings(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "ALL") State state, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getBookingsByOwnerAndState(state, userId, from, size);
    }
}
