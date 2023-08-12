package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestBody BookingInputDto object, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.create(object, userId);
    }

    @PatchMapping("{id}")
    public BookingDto approve(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean approved, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.approve(id, userId, approved);
    }

    @GetMapping("{id}")
    public BookingDto get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.get(id, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "ALL") State state, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getBookingsByBookerAndState(state, userId, from, size);
    }

    @GetMapping("owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "ALL") State state, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getBookingsByOwnerAndState(state, userId, from, size);
    }
}
