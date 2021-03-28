package com.example.hotel.domain.feature.impl;

import com.example.hotel.domain.feature.CancelBookingById;
import com.example.hotel.domain.model.BookingRepository;
import com.example.hotel.domain.model.CancelBookingInput;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;

@Named
@AllArgsConstructor
public class CancelBookingByIdImpl implements CancelBookingById {

  private final BookingRepository bookingRepository;

  @Override
  public Mono<Void> handle(CancelBookingInput cancelBookingInput) {
    return bookingRepository
        .findBookingById(cancelBookingInput.getBookingId())
        .flatMap(bookingRepository::delete);
  }
}
