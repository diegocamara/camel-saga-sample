package com.example.hotel.domain.feature;

import com.example.hotel.domain.model.CancelBookingInput;
import reactor.core.publisher.Mono;

public interface CancelBookingById {
  Mono<Void> handle(CancelBookingInput cancelBookingInput);
}
