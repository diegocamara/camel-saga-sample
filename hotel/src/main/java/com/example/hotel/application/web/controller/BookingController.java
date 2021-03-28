package com.example.hotel.application.web.controller;

import com.example.hotel.application.web.controller.transaction.CreateBookingTransaction;
import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/booking")
public class BookingController {

  private final CreateBookingTransaction createBookingTransaction;

  public Mono<ResponseEntity<BookingResponse>> createBooking(
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody BookingRequest bookingRequest) {
    return createBookingTransaction.execute(transactionReference, bookingRequest);
  }
}
