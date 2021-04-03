package com.example.orders.infrastructure.repository.springdata.document;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDocument {
  private LocalDateTime date;
}
