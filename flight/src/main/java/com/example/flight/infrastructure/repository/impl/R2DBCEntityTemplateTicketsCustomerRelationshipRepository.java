package com.example.flight.infrastructure.repository.impl;

import com.example.flight.domain.model.Customer;
import com.example.flight.domain.model.Ticket;
import com.example.flight.domain.model.TicketCustomerRelationship;
import com.example.flight.domain.model.TicketsCustomerRelationshipRepository;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateTicketsCustomerRelationshipRepository
    implements TicketsCustomerRelationshipRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<Void> save(TicketCustomerRelationship ticketCustomerRelationship) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql("INSERT INTO ticket_customer (ticket_id, customer_id) VALUES (:ticketId, :customerId)")
        .bind("ticketId", ticketCustomerRelationship.getTicket().getId().toString())
        .bind("customerId", ticketCustomerRelationship.getCustomer().getId().toString())
        .fetch()
        .rowsUpdated()
        .map(rowsUpdated -> rowsUpdated)
        .then();
  }

  @Override
  public Mono<TicketCustomerRelationship> findById(UUID ticketId, UUID customerId) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "SELECT * FROM ticket_customer AS ticket_customer"
                + " INNER JOIN tickets as tickets ON tickets.id = ticket_customer.ticket_id"
                + " WHERE ticket_id = :ticketId and customer_id = :customerId")
        .bind("ticketId", ticketId.toString())
        .bind("customerId", customerId.toString())
        .map(row -> row)
        .first()
        .map(this::ticketCustomerRelationship);
  }

  @Override
  public Mono<Void> delete(TicketCustomerRelationship ticketCustomerRelationship) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "DELETE FROM ticket_customer"
                + " WHERE ticket_id = :ticketId and customer_id = :customerId")
        .bind("ticketId", ticketCustomerRelationship.getTicket().getId().toString())
        .bind("customerId", ticketCustomerRelationship.getCustomer().getId().toString())
        .fetch()
        .rowsUpdated()
        .map(rowsUpdated -> rowsUpdated)
        .then();
  }

  private TicketCustomerRelationship ticketCustomerRelationship(Row row) {
    final var ticket = new Ticket();
    ticket.setId(row.get("id", UUID.class));
    ticket.setPrice(row.get("price", BigDecimal.class));
    ticket.setFrom(row.get("location_from", String.class));
    ticket.setDestination(row.get("location_destination", String.class));
    final var customer = new Customer(row.get("customer_id", UUID.class));
    return new TicketCustomerRelationship(ticket, customer);
  }
}
