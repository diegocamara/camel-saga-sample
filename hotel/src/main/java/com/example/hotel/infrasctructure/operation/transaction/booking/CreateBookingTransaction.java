package com.example.hotel.infrasctructure.operation.transaction.booking;

import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.domain.feature.CreateBooking;
import com.example.hotel.infrasctructure.operation.OperationsRepository;
import com.example.hotel.infrasctructure.operation.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CreateBookingTransaction {

  private final CreateBooking createBooking;
  private final OperationsRepository<BookingOperation> bookingOperationsRepository;

  @Transactional
  public Mono<BookingOperation> execute(BookingRequest bookingRequest, UUID operationReference) {
    return bookingOperationsRepository
        .findByOperationReference(operationReference)
        .switchIfEmpty(
            createBooking
                .handle(bookingRequest.toCreateBookingInput())
                .flatMap(
                    booking -> {
                      final var bookingResponse = new BookingResponse(booking);
                      final var bookingOperation =
                          new BookingOperation(operationReference, bookingResponse);
                      bookingOperation.setStatus(Status.EXECUTED);
                      return bookingOperationsRepository
                          .save(bookingOperation)
                          .thenReturn(bookingOperation);
                    }));
  }
}
