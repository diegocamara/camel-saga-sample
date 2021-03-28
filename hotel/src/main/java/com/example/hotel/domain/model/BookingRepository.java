package com.example.hotel.domain.model;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookingRepository {
  Mono<Void> save(Booking booking);

  Mono<Booking> findBookingById(UUID id);

  Mono<Void> delete(Booking booking);
}
