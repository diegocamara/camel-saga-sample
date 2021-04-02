package com.example.orders.infrastructure.web.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BookingResponse {
  private UUID id;
  private UUID bedroomId;
}
