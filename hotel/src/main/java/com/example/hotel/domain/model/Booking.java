package com.example.hotel.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Booking {
  private UUID id;
  private Customer customer;
  private Bedroom bedroom;
  private Period period;
}
