package com.example.hotel.application.web.controller.transaction;

import com.example.hotel.domain.feature.CancelBookingById;
import com.example.hotel.domain.model.CancelBookingInput;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CancelBookingTransaction {

  private final CancelBookingById cancelBookingById;

  @Transactional
  public Mono<ResponseEntity<?>> execute(UUID bookingId) {
    return cancelBookingById
        .handle(new CancelBookingInput(bookingId))
        .map(unused -> ResponseEntity.ok().build());
  }
}
