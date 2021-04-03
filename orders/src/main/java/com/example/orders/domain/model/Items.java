package com.example.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Items {
  private FlightTicketPurchase flightTicketPurchase;
  private HotelBooking hotelBooking;

  public boolean flightTicketPurchaseExists() {
    return this.flightTicketPurchase != null;
  }

  public boolean hotelBookingExists() {
    return this.hotelBooking != null;
  }
}
