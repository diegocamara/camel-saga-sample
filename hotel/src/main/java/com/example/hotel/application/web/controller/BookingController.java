package com.example.hotel.application.web.controller;

import com.example.hotel.application.web.controller.transaction.CreateBookingTransaction;
import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.domain.feature.CancelBookingById;
import com.example.hotel.domain.model.CancelBookingInput;
import com.example.hotel.infrasctructure.repository.OperationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/booking")
public class BookingController {

  private final CreateBookingTransaction createBookingTransaction;
  private final CancelBookingById cancelBookingById;
  private final OperationsRepository operationsRepository;

  @PostMapping
  public Mono<ResponseEntity<BookingResponse>> createBooking(
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody BookingRequest bookingRequest) {
    return createBookingTransaction
        .execute(transactionReference, bookingRequest)
        .onErrorResume(
            throwable ->
                operationsRepository
                    .findById(transactionReference)
                    .map(operationTable -> ResponseEntity.ok(new BookingResponse(operationTable))));
  }

  @DeleteMapping("/{bookingId}")
  public Mono<ResponseEntity<?>> cancelBooking(@PathVariable("bookingId") UUID bookingId) {
    return cancelBookingById
        .handle(new CancelBookingInput(bookingId))
        .map(unused -> ResponseEntity.ok().build());
  }
}
