package com.example.orders.infrastructure.repository;

import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrdersRepository;
import com.example.orders.infrastructure.repository.springdata.document.OrderDocument;
import com.example.orders.infrastructure.repository.springdata.repository.SpringDataOrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MongoDBOrdersRepository implements OrdersRepository {

  private final SpringDataOrdersRepository springDataOrdersRepository;

  @Override
  public void save(Order order) {
    springDataOrdersRepository.save(new OrderDocument(order));
  }
}
