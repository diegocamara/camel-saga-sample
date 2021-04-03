package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.Timeline;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TimelineDocument {
  private List<EventDocument> events = new LinkedList<>();

  public TimelineDocument(Timeline timeline) {
    this.events =
        timeline.getEvents().stream()
            .map(event -> new EventDocument())
            .collect(Collectors.toList());
  }

  public Optional<BuyTicketResponseDocument> buyTicketResponseDocument() {
    return this.events.stream()
        .filter(
            eventDocument ->
                BuyTicketResponseDocument.class.isAssignableFrom(eventDocument.getClass()))
        .map(eventDocument -> (BuyTicketResponseDocument) eventDocument)
        .findFirst();
  }

  public Optional<BookingResponseDocument> bookingResponseDocument() {
    return this.events.stream()
        .filter(
            eventDocument ->
                BookingResponseDocument.class.isAssignableFrom(eventDocument.getClass()))
        .map(eventDocument -> (BookingResponseDocument) eventDocument)
        .findFirst();
  }
}
