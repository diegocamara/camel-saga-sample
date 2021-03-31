package com.example.hotel.application.web.controller;

import com.example.hotel.application.web.controller.transaction.CancelBookingTransaction;
import com.example.hotel.application.web.controller.transaction.CreateBookingTransaction;
import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
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
  private final CancelBookingTransaction cancelBookingTransaction;
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
    return cancelBookingTransaction.execute(bookingId);
  }
}
