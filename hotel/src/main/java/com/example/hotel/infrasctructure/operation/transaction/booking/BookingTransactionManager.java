package com.example.hotel.infrasctructure.operation.transaction.booking;

import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.infrasctructure.operation.OperationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingTransactionManager {

  private final CreateBookingTransaction createBookingTransaction;
  private final CancelBookingTransaction cancelBookingTransaction;
  private final OperationsRepository<BookingOperation> bookingOperationsRepository;

  public Mono<BookingOperation> execute(BookingRequest bookingRequest, UUID operationReference) {
    return createBookingTransaction
        .execute(bookingRequest, operationReference)
        .onErrorResume(
            throwable -> bookingOperationsRepository.findByOperationReference(operationReference));
  }

  public Mono<Void> rollback(UUID operationReference) {
    return cancelBookingTransaction.execute(operationReference).then();
  }
}
