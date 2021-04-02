package com.example.orders.infrastructure.repository;

import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrdersRepository;
import lombok.AllArgsConstructor;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MongoDBOrdersRepository implements OrdersRepository {

  @EndpointInject("direct:create-order")
  private final ProducerTemplate producerTemplate;

  @Override
  public void save(Order order) {
    producerTemplate.sendBody(order);
  }
}
