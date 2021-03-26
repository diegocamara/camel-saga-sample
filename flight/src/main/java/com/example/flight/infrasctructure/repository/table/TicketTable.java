package com.example.flight.infrasctructure.repository.table;

import com.example.flight.domain.model.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("TICKETS")
@NoArgsConstructor
public class TicketTable {
  @Id private UUID id;

  @Column("customer_id")
  private UUID customerId;

  @Column("location_from")
  private String from;

  @Column("location_destination")
  private String destination;

  public TicketTable(Ticket ticket) {
    this.id = ticket.getId();
    this.customerId = ticket.getCustomer().getId();
    this.from = ticket.getFrom();
    this.destination = ticket.getDestination();
  }
}
