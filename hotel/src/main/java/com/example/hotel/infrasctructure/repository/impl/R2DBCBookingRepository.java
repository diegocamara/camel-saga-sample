package com.example.hotel.infrasctructure.repository.impl;

import com.example.hotel.domain.model.Booking;
import com.example.hotel.domain.model.BookingRepository;
import com.example.hotel.domain.model.Customer;
import com.example.hotel.infrasctructure.repository.reactive.ReactiveBookingRepository;
import com.example.hotel.infrasctructure.repository.table.BookingTable;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCBookingRepository implements BookingRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;
  private final ReactiveBookingRepository reactiveBookingRepository;

  @Override
  public Mono<Void> save(Booking booking) {
    return r2dbcEntityTemplate.insert(new BookingTable(booking)).then();
  }

  @Override
  public Mono<Booking> findBookingById(UUID id) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "SELECT * FROM booking AS booking"
                + " INNER JOIN bedrooms as bedrooms ON bedrooms.id = booking.bedroom_id WHERE booking.id = : bookingId")
        .bind("bookingId", id.toString())
        .map(row -> row)
        .first()
        .map(this::booking);
  }

  private Booking booking(Row row) {
    final var id = row.get("id", UUID.class);
    final var customer = new Customer(row.get("customer_id", UUID.class));
    //    final var period = new Period(bookingTable.getFrom(), bookingTable.getTo());
    return new Booking(id, customer, null, null);
  }
}
