package com.example.hotel.application.web.controller;

import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.infrasctructure.operation.transaction.booking.BookingTransactionManager;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/booking")
public class BookingController {

  public static final String OPERATION_REFERENCE_HEADER = "operation-reference";
  private final BookingTransactionManager bookingTransactionManager;

  @PostMapping
  public Mono<ResponseEntity<BookingResponse>> createBooking(
      @RequestHeader(OPERATION_REFERENCE_HEADER) UUID operationReference,
      @RequestBody BookingRequest bookingRequest) {
    return bookingTransactionManager
        .execute(bookingRequest, operationReference)
        .map(
            bookingOperation ->
                ResponseEntity.status(HttpStatus.CREATED).body(bookingOperation.getOutput()));
  }

  @DeleteMapping
  public Mono<ResponseEntity<?>> cancelBooking(
      @RequestHeader(OPERATION_REFERENCE_HEADER) UUID operationReference) {
    return bookingTransactionManager
        .rollback(operationReference)
        .map(unused -> ResponseEntity.ok().build());
  }
}
