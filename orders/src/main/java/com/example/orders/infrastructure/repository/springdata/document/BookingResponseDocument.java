package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.infrastructure.web.model.BookingResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingResponseDocument extends EventDocument {
  private UUID bookingId;
  private UUID bedroomId;

  public BookingResponseDocument(BookingResponse bookingResponse) {
    this.bookingId = bookingResponse.getId();
    this.bedroomId = bookingResponse.getBedroomId();
    this.setDate(LocalDateTime.now());
  }
}
