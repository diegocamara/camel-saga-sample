package com.example.hotel.domain.feature;

import com.example.hotel.domain.model.Booking;
import com.example.hotel.domain.model.CreateBookingInput;
import reactor.core.publisher.Mono;

public interface CreateBooking {
  Mono<Booking> handle(CreateBookingInput createBookingInput);
}
