package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.infrastructure.web.model.BuyTicketResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BuyTicketResponseDocument extends EventDocument {
  private UUID ticketId;
  private BigDecimal price;
  private String from;
  private String destination;

  public BuyTicketResponseDocument(BuyTicketResponse buyTicketResponse) {
    final var ticket = buyTicketResponse.getTicket();
    this.ticketId = ticket.getId();
    this.price = ticket.getPrice();
    this.from = ticket.getFrom();
    this.destination = ticket.getDestination();
    this.setDate(LocalDateTime.now());
  }
}
