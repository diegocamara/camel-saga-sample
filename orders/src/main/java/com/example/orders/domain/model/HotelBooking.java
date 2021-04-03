package com.example.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class HotelBooking {
  private UUID bedroomId;
  private LocalDateTime from;
  private LocalDateTime to;
}
