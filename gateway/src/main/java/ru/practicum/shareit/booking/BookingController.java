package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.UnsupportedStateMessage;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient client;

	@PostMapping
	public ResponseEntity<Object> create(@Valid @RequestBody BookingInputDto dto, @RequestHeader(Config.userHeaderName) Long userId) {
		log.info("Create booking {}, userId={}", dto, userId);

		if (dto.getEnd().isBefore(dto.getStart())) {
			return new ResponseEntity<>("Конец бронирования не может быть раньше начала", HttpStatus.BAD_REQUEST);
		} else if (dto.getEnd().isEqual(dto.getStart())) {
			return new ResponseEntity<>("Конец бронирования не может быть равен началу", HttpStatus.BAD_REQUEST);
		}

		return client.create(dto, userId);
	}

	@PatchMapping("{id}")
	public ResponseEntity<Object> approve(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean approved, @RequestHeader(Config.userHeaderName) Long userId) {
		log.info("Change booking {}, userId={}, approved={}", id, userId, approved);
		return client.approve(id, approved, userId);
	}

	@GetMapping("{id}")
	public ResponseEntity<Object> get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
		log.info("Get booking {}, userId={}", id, userId);
		return client.get(id, userId);
	}

	@GetMapping("owner")
	public ResponseEntity<Object> getBookingsByOwner(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "ALL") String state, @RequestHeader(Config.userHeaderName) Long userId) {
		var realState = State.from(state);
		if (realState.isEmpty()) {
			return new ResponseEntity<>(new UnsupportedStateMessage("Unknown state: " + state), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (size <= 0 || from < 0) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.info("Get booking by owner {}, from={}, size={}, state={}", userId, from, size, state);
		return client.getBookingsByOwner(from, size, realState.get(), userId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsByBooker(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "ALL") String state, @RequestHeader(Config.userHeaderName) Long userId) {
		var realState = State.from(state);
		if (realState.isEmpty()) {
			return new ResponseEntity<>(new UnsupportedStateMessage("Unknown state: " + state), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (size <= 0 || from < 0) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.info("Get booking by booker {}, from={}, size={}, state={}", userId, from, size, state);
		return client.getBookingsByBooker(from, size, realState.get(), userId);
	}
}
