package com.example.orders.infrastructure.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BookingRequest {
  private UUID bedroomId;
  private UUID customerId;
  private LocalDateTime from;
  private LocalDateTime to;
}
