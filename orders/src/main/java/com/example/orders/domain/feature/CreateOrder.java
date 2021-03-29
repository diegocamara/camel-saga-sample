package com.example.orders.domain.feature;

import com.example.orders.domain.model.CreateOrderInput;
import com.example.orders.domain.model.Order;

public interface CreateOrder {
  Order handle(CreateOrderInput createOrderInput);
}
