package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.HotelBooking;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class HotelBookingDocument {
  private UUID bedroomId;
  private LocalDateTime from;
  private LocalDateTime to;

  public HotelBookingDocument(HotelBooking hotelBooking) {
    this.bedroomId = hotelBooking.getBedroomId();
    this.from = hotelBooking.getFrom();
    this.to = hotelBooking.getTo();
  }
}
