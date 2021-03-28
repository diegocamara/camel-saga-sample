package com.example.hotel.infrasctructure.repository.impl;

import com.example.hotel.domain.model.Booking;
import com.example.hotel.infrasctructure.repository.OperationsRepository;
import com.example.hotel.infrasctructure.repository.table.BookingTable;
import com.example.hotel.infrasctructure.repository.table.Operation;
import com.example.hotel.infrasctructure.repository.table.OperationTable;
import com.example.hotel.infrasctructure.repository.utils.RepositoryUtils;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCOperationsRepository implements OperationsRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<OperationTable> findById(UUID id) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "SELECT operations.id AS operation_id,"
                + " operations.operation_name AS operation_name,"
                + " booking.id AS booking_id,"
                + " booking.bedroom_id AS booking_bedroom_id,"
                + " booking.customer_id AS booking_customer_id,"
                + " booking.period_from AS booking_period_from,"
                + " booking.period_to AS booking_period_to,"
                + " bedrooms.id AS bedroom_id,"
                + " bedrooms.description AS bedroom_description"
                + " FROM operations AS operations"
                + " INNER JOIN booking AS booking ON booking.id = operations.booking_id"
                + " INNER JOIN bedrooms AS bedrooms ON bedrooms.id = booking.bedroom_id"
                + " WHERE operations.id = :operationId")
        .bind("operationId", id)
        .map(row -> row)
        .first()
        .map(this::operationTable);
  }

  @Override
  public Mono<OperationTable> create(UUID id, Operation operation, Booking booking) {
    return r2dbcEntityTemplate
        .insert(new OperationTable(id, operation, booking))
        .map(operationTable -> operationTable);
  }

  private OperationTable operationTable(Row row) {
    final var operationTable = new OperationTable();
    operationTable.setId(row.get("operation_id", UUID.class));
    operationTable.setOperation(Operation.valueOf(row.get("operation_name", String.class)));
    operationTable.setBooking(new BookingTable(RepositoryUtils.booking(row)));
    operationTable.setBookingId(operationTable.getBooking().getId());
    return operationTable;
  }
}
