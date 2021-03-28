package com.example.hotel.application.messaging.subscriber;

import com.example.hotel.application.messaging.model.BookingRequestMessage;
import com.example.hotel.domain.feature.CreateBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

// @Component
public class BookingSubscriber {

  @Autowired
  @Qualifier("bookingRequestMessageFlux")
  private Flux<BookingRequestMessage> bookingRequestMessageFlux;

  @Autowired private CreateBooking createBooking;

  @PostConstruct
  public void postConstruct() {
    bookingRequestMessageFlux
        .flatMap(
            bookingRequestMessage ->
                createBooking.handle(bookingRequestMessage.toCreateBookingInput()))
        .subscribe();
  }
}
