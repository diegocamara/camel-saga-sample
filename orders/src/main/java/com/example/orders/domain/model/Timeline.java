package com.example.orders.domain.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Timeline {
  private List<Event> events = new LinkedList<>();
}
