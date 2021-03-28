package com.example.hotel.infrasctructure.repository.table;

import com.example.hotel.domain.model.Booking;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("operations")
@NoArgsConstructor
public class OperationTable {
  @Id private UUID id;

  @Column("booking_id")
  private UUID bookingId;

  @Column("operation_name")
  private Operation operation;

  @Transient private BookingTable booking;

  public OperationTable(UUID id, Operation operation, Booking booking) {
    this.id = id;
    this.operation = operation;
    this.bookingId = booking.getId();
  }
}
