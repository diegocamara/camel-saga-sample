package com.example.orders.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;

@Data
public abstract class Event implements Comparator<Event> {

  private final LocalDateTime date;

  public Event(LocalDateTime date) {
    this.date = date;
  }

  @Override
  public int compare(Event o1, Event o2) {
    return o1.getDate().isBefore(o2.getDate()) ? 1 : o1.getDate().isEqual(o2.getDate()) ? 0 : -1;
  }
}
