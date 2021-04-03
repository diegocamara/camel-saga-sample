package com.example.orders.application.web.model;

import com.example.orders.domain.model.HotelBooking;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HotelBookingWebModel {
  private UUID bedroomId;
  private LocalDateTime from;
  private LocalDateTime to;

  public HotelBooking toHotelBooking() {
    return new HotelBooking(this.bedroomId, this.from, this.to);
  }
}
