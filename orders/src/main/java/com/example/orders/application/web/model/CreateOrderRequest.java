package com.example.orders.application.web.model;

import com.example.orders.domain.model.CreateOrderInput;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {
  private UUID customerId;
  private ItemsWebModel items = new ItemsWebModel();

  public CreateOrderInput toCreateOrderInput() {
    return new CreateOrderInput(
        this.customerId, this.items.flightTicketPurchase(), this.items.hotelBooking());
  }
}
