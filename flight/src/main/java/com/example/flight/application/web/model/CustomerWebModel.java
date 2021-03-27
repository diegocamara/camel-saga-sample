package com.example.flight.application.web.model;

import com.example.flight.domain.model.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomerWebModel {
  private UUID id;

  public CustomerWebModel(Customer customer) {
    this.id = customer.getId();
  }
}
