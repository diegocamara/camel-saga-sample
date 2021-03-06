package com.example.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateOrderInput {
  private UUID customerId;
  private FlightTicketPurchase flightTicketPurchase;
  private HotelBooking hotelBooking;
}
