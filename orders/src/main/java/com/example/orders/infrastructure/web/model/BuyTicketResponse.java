package com.example.orders.infrastructure.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BuyTicketResponse {
  private TicketWebModel ticket;
  private CustomerWebModel customer;
  private UUID operationReference;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BuyTicketResponse that = (BuyTicketResponse) o;
    return Objects.equals(ticket, that.ticket)
        && Objects.equals(customer, that.customer)
        && Objects.equals(operationReference, that.operationReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ticket, customer, operationReference);
  }
}
