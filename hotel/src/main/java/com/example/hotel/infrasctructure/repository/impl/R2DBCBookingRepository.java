package com.example.hotel.infrasctructure.repository.impl;

import com.example.hotel.domain.model.Booking;
import com.example.hotel.domain.model.BookingRepository;
import com.example.hotel.infrasctructure.repository.reactive.ReactiveBookingRepository;
import com.example.hotel.infrasctructure.repository.table.BookingTable;
import com.example.hotel.infrasctructure.repository.utils.RepositoryUtils;
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
            "SELECT booking.id AS booking_id,"
                + " booking.bedroom_id AS booking_bedroom_id,"
                + " booking.customer_id AS booking_customer_id,"
                + " booking.period_from AS booking_period_from,"
                + " booking.period_to AS booking_period_to,"
                + " bedrooms.id AS bedroom_id,"
                + " bedrooms.description AS bedroom_description,"
                + " bedrooms.price AS bedroom_price"
                + " FROM booking AS booking"
                + " INNER JOIN bedrooms AS bedrooms ON bedrooms.id = booking.bedroom_id WHERE booking.id = :bookingId")
        .bind("bookingId", id)
        .map(row -> row)
        .first()
        .map(RepositoryUtils::booking);
  }

  @Override
  public Mono<Void> delete(Booking booking) {
    return reactiveBookingRepository.deleteById(booking.getId());
  }
}
