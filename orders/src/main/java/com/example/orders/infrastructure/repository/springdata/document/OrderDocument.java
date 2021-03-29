package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Document(collection = "#{@environment.getProperty('collections.orders-collection')}")
public class OrderDocument {

  @Id private UUID id;
  private CustomerDocument customer;
  private List<Item> items;
  private TimelineDocument timeline;

  public OrderDocument(Order order) {
    this.id = order.getId();
    this.customer = new CustomerDocument(order.getCustomer());
    this.items = order.getItems();
    this.timeline = new TimelineDocument(order.getTimeline());
  }

  @Data
  @NoArgsConstructor
  public static class CustomerDocument {
    private UUID id;

    public CustomerDocument(Customer customer) {
      this.id = customer.getId();
    }
  }

  @Data
  @NoArgsConstructor
  public static class TimelineDocument {
    private List<EventDocument> events;

    public TimelineDocument(Timeline timeline) {
      this.events =
          timeline.getEvents().stream().map(EventDocument::new).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    public static class EventDocument {
      private LocalDateTime date;
      private String eventClass;

      public EventDocument(Event event) {
        this.date = event.getDate();
        this.eventClass = event.getClass().getCanonicalName();
      }
    }
  }
}
