package com.example.hotel.application.web.model;

import com.example.hotel.domain.model.Booking;
import lombok.Data;

import java.util.UUID;

@Data
public class BookingResponse {
  private UUID id;
  private UUID bedroomId;

  public BookingResponse(Booking booking) {
    this.id = booking.getId();
    this.bedroomId = booking.getBedroom().getId();
  }
}
