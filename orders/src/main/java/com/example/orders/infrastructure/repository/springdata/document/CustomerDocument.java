package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomerDocument {
  private UUID id;

  public CustomerDocument(Customer customer) {
    this.id = customer.getId();
  }
}
