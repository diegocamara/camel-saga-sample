package com.example.orders.application.web.model;

import com.example.orders.domain.model.FlightTicketPurchase;
import com.example.orders.domain.model.HotelBooking;
import lombok.Data;

@Data
public class ItemsWebModel {
  private FlightTicketPurchaseWebModel flightTicketPurchaseWebModel;
  private HotelBookingWebModel hotelBookingWebModel;

  public FlightTicketPurchase flightTicketPurchase() {
    return this.flightTicketPurchaseWebModel != null
        ? this.flightTicketPurchaseWebModel.toFlightTicketPurchase()
        : null;
  }

  public HotelBooking hotelBooking() {
    return this.hotelBookingWebModel != null ? this.hotelBookingWebModel.toHotelBooking() : null;
  }
}
