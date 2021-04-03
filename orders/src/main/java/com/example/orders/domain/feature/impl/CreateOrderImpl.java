package com.example.orders.domain.feature.impl;

import com.example.orders.domain.feature.CreateOrder;
import com.example.orders.domain.model.*;
import lombok.AllArgsConstructor;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.UUID;

@Named
@AllArgsConstructor
public class CreateOrderImpl implements CreateOrder {

  private final OrdersRepository ordersRepository;

  @Override
  public Order handle(CreateOrderInput createOrderInput) {
    final var order = createOrder(createOrderInput);
    ordersRepository.save(order);
    return order;
  }

  private Order createOrder(CreateOrderInput createOrderInput) {
    final var customer = new Customer(createOrderInput.getCustomerId());
    final var items =
        new Items(createOrderInput.getFlightTicketPurchase(), createOrderInput.getHotelBooking());
    return new Order(UUID.randomUUID(), customer, items, new Timeline(), LocalDateTime.now());
  }
}
