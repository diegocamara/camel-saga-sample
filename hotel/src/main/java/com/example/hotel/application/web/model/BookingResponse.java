package com.example.hotel.application.web.model;

import com.example.hotel.domain.model.Booking;
import com.example.hotel.infrasctructure.repository.table.OperationTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class BookingResponse {
  private UUID id;
  private UUID bedroomId;

  public BookingResponse(Booking booking) {
    this.id = booking.getId();
    this.bedroomId = booking.getBedroom().getId();
  }

  public BookingResponse(OperationTable operationTable) {
    final var booking = operationTable.getBooking();
    this.id = booking.getId();
    this.bedroomId = booking.getBedroomId();
  }
}
