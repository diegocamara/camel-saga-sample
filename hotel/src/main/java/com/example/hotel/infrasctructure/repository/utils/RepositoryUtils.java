package com.example.hotel.infrasctructure.repository.utils;

import com.example.hotel.domain.model.Bedroom;
import com.example.hotel.domain.model.Booking;
import com.example.hotel.domain.model.Customer;
import com.example.hotel.domain.model.Period;
import io.r2dbc.spi.Row;

import java.time.LocalDateTime;
import java.util.UUID;

public class RepositoryUtils {

  public static Booking booking(Row row) {
    final var id = row.get("booking_id", UUID.class);
    final var customer = new Customer(row.get("booking_customer_id", UUID.class));
    final var period =
        new Period(
            row.get("booking_period_from", LocalDateTime.class),
            row.get("booking_period_to", LocalDateTime.class));
    final var bedroom =
        new Bedroom(
            row.get("bedroom_id", UUID.class), row.get("bedroom_description", String.class));
    return new Booking(id, customer, bedroom, period);
  }
}
