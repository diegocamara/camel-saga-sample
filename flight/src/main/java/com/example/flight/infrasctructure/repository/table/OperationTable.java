package com.example.flight.infrasctructure.repository.table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("OPERATIONS")
@NoArgsConstructor
public class OperationTable {
  @Id private UUID id;

  @Column("ticket_id")
  private UUID ticketId;

  @Column("operation_name")
  private Operation operation;

  @Transient private TicketTable ticket;

  public OperationTable(UUID id, Operation operation, TicketTable ticket) {
    this.id = id;
    this.operation = operation;
    this.ticketId = ticket.getId();
    this.ticket = ticket;
  }
}
