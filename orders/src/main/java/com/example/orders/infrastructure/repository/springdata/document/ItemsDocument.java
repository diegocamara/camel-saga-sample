package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.Items;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemsDocument {

  private FlightTicketPurchaseDocument flightTicketPurchaseDocument;
  private HotelBookingDocument hotelBookingDocument;

  public ItemsDocument(Items items) {
    this.flightTicketPurchaseDocument =
        new FlightTicketPurchaseDocument(items.getFlightTicketPurchase());
    this.hotelBookingDocument = new HotelBookingDocument(items.getHotelBooking());
  }
}
