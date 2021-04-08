package com.example.hotel.infrasctructure.operation.transaction.booking;

import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.infrasctructure.operation.Operation;

import java.util.UUID;

public class BookingOperation extends Operation<BookingResponse> {

  public BookingOperation(UUID id, BookingResponse bookingResponse) {
    super(id, bookingResponse);
  }
}
