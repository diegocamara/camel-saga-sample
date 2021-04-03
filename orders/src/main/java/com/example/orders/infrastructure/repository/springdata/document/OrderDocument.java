package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.Item;
import com.example.orders.domain.model.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "#{@environment.getProperty('collections.orders-collection')}")
public class OrderDocument {

  @Id private UUID id;
  private CustomerDocument customer;
  private List<Item> items;
  private TimelineDocument timeline = new TimelineDocument();
  @Version private Long version;

  public OrderDocument(Order order) {
    this.id = order.getId();
    this.customer = new CustomerDocument(order.getCustomer());
    this.items = order.getItems();
    this.timeline = new TimelineDocument(order.getTimeline());
  }
}
