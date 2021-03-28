package com.example.hotel.application.messaging.model;

import com.example.hotel.domain.model.CreateBookingInput;
import com.example.hotel.domain.model.Period;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingRequestMessage {
  private UUID bedroomId;
  private UUID customerId;
  private LocalDateTime from;
  private LocalDateTime to;

  public CreateBookingInput toCreateBookingInput() {
    return new CreateBookingInput(this.bedroomId, this.customerId, new Period(this.from, this.to));
  }
}
