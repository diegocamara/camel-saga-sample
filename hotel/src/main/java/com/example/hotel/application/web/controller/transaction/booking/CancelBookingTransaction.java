package com.example.hotel.application.web.controller.transaction.booking;

import com.example.hotel.domain.feature.CancelBookingById;
import com.example.hotel.domain.model.CancelBookingInput;
import com.example.hotel.infrasctructure.operation.OperationNotRegisteredException;
import com.example.hotel.infrasctructure.operation.OperationsRepository;
import com.example.hotel.infrasctructure.operation.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CancelBookingTransaction {

  private final CancelBookingById cancelBookingById;
  private final OperationsRepository<BookingOperation> bookingOperationsRepository;

  @Transactional
  public Mono<BookingOperation> execute(UUID operationReference) {
    return bookingOperationsRepository
        .findByOperationReference(operationReference)
        .switchIfEmpty(Mono.error(OperationNotRegisteredException::new))
        .filter(BookingOperation::isExecuted)
        .flatMap(
            bookingOperation -> {
              final var bookingResponse = bookingOperation.getOutput();
              bookingOperation.setStatus(Status.ROLLBACK);
              return cancelBookingById
                  .handle(new CancelBookingInput(bookingResponse.getId()))
                  .then(
                      bookingOperationsRepository
                          .update(bookingOperation)
                          .thenReturn(bookingOperation));
            });
  }
}
