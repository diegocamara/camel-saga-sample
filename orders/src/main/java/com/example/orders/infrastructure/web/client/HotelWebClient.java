package com.example.orders.infrastructure.web.client;

import com.example.orders.infrastructure.web.model.BookingRequest;
import com.example.orders.infrastructure.web.model.BookingResponse;

import java.util.UUID;

public interface HotelWebClient {

  BookingResponse createBooking(BookingRequest bookingRequest);

  void cancelBooking(UUID bookingId);
}
