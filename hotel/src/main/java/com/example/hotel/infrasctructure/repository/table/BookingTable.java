package com.example.hotel.infrasctructure.repository.table;

import com.example.hotel.domain.model.Booking;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("booking")
public class BookingTable {
  @Id private UUID id;

  @Column("bedroom_id")
  private UUID bedroomId;

  @Column("customer_id")
  private UUID customerId;

  @Column("period_from")
  private LocalDateTime from;

  @Column("period_to")
  private LocalDateTime to;

  public BookingTable(Booking booking) {
    this.id = booking.getId();
    this.bedroomId = booking.getBedroom().getId();
    this.customerId = booking.getCustomer().getId();
    this.from = booking.getPeriod().getFrom();
    this.to = booking.getPeriod().getTo();
  }
}
