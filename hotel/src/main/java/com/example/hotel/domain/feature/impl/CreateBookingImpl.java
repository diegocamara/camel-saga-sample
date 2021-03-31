package com.example.hotel.domain.feature.impl;

import com.example.hotel.domain.feature.AccountDebit;
import com.example.hotel.domain.feature.CreateBooking;
import com.example.hotel.domain.model.*;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.util.UUID;

@Named
@AllArgsConstructor
public class CreateBookingImpl implements CreateBooking {

  private final AccountDebit accountDebit;
  private final BedroomsRepository bedroomsRepository;
  private final BookingRepository bookingRepository;

  @Override
  public Mono<Booking> handle(CreateBookingInput createBookingInput) {
    return bedroomsRepository
        .findBedroomById(createBookingInput.getBedroomId())
        .flatMap(
            bedroom ->
                createBooking(bedroom, createBookingInput)
                    .flatMap(
                        booking ->
                            bookingRepository
                                .save(booking)
                                .then(
                                    accountDebit
                                        .handle(
                                            new AccountDebitInput(
                                                createBookingInput.getCustomerId(),
                                                bedroom.getPrice()))
                                        .thenReturn(booking))));
  }

  private Mono<Booking> createBooking(Bedroom bedroom, CreateBookingInput createBookingInput) {
    final var customer = new Customer(createBookingInput.getCustomerId());
    return Mono.just(
        new Booking(UUID.randomUUID(), customer, bedroom, createBookingInput.getPeriod()));
  }
}
