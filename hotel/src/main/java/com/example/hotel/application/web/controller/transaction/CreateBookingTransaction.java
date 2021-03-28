package com.example.hotel.application.web.controller.transaction;

import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.domain.feature.CreateBooking;
import com.example.hotel.infrasctructure.repository.OperationsRepository;
import com.example.hotel.infrasctructure.repository.table.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CreateBookingTransaction {

  private final OperationsRepository operationsRepository;
  private final CreateBooking createBooking;

  @Transactional
  public Mono<ResponseEntity<BookingResponse>> execute(
      UUID transactionReference, BookingRequest bookingRequest) {
    return createBooking
        .handle(bookingRequest.toCreateBookingInput())
        .flatMap(
            booking ->
                operationsRepository
                    .create(transactionReference, Operation.BOOKING, booking)
                    .map(
                        operationTable ->
                            ResponseEntity.status(HttpStatus.CREATED)
                                .body(new BookingResponse(booking))));
  }
}
