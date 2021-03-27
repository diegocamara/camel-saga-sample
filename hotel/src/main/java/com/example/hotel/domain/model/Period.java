package com.example.hotel.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Period {
  private final LocalDateTime from;
  private final LocalDateTime to;
}
