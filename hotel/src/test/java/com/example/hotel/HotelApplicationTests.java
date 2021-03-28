package com.example.hotel;

import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.domain.model.Bedroom;
import com.example.hotel.domain.model.Booking;
import com.example.hotel.domain.model.Customer;
import com.example.hotel.domain.model.Period;
import com.example.hotel.infrasctructure.repository.table.Operation;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.UUID;

class HotelApplicationTests extends IntegrationTest {

  private final UUID customerId = UUID.randomUUID();
  private final String TRANSACTION_REFERENCE_HEADER = "transaction-reference";

  @Test
  void shouldCreateBooking() {

    final var bedroom = new Bedroom(UUID.randomUUID(), "001");

    r2dbcBedroomsRepository.save(bedroom).block();

    final var now = LocalDateTime.now();

    final var bookingRequest = new BookingRequest();
    bookingRequest.setCustomerId(customerId);
    bookingRequest.setBedroomId(bedroom.getId());
    bookingRequest.setFrom(now);
    bookingRequest.setTo(now.plusDays(2));

    final var transactionReference = UUID.randomUUID();

    final var bookingRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                TRANSACTION_REFERENCE_HEADER,
                transactionReference)
            .body(writeValueAsString(bookingRequest));

    final var bookingResponse = bookingRequestSpecification.post("/booking");

    final var bookingResponseModel =
        readValue(bookingResponse.body().print(), BookingResponse.class);

    bookingResponse
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body(
            "id",
            CoreMatchers.notNullValue(),
            "bedroomId",
            CoreMatchers.is(bedroom.getId().toString()));

    final var storedBooking =
        r2dbcBookingRepository.findBookingById(bookingResponseModel.getId()).block();

    Assertions.assertNotNull(storedBooking);
  }

  @Test
  void testingCreateBookingEndpointIdempotency() {

    final var bedroom = new Bedroom(UUID.randomUUID(), "001");

    r2dbcBedroomsRepository.save(bedroom).block();

    final var now = LocalDateTime.now();

    final var bookingRequest = new BookingRequest();
    bookingRequest.setCustomerId(customerId);
    bookingRequest.setBedroomId(bedroom.getId());
    bookingRequest.setFrom(now);
    bookingRequest.setTo(now.plusDays(2));

    final var transactionReference = UUID.randomUUID();

    final var bookingRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                TRANSACTION_REFERENCE_HEADER,
                transactionReference)
            .body(writeValueAsString(bookingRequest));

    final var firstBookingResponse = bookingRequestSpecification.post("/booking");

    firstBookingResponse
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body(
            "id",
            CoreMatchers.notNullValue(),
            "bedroomId",
            CoreMatchers.is(bedroom.getId().toString()));

    final var secondBookingResponse = bookingRequestSpecification.post("/booking");

    final var secondBookingResponseModel =
        readValue(secondBookingResponse.body().print(), BookingResponse.class);

    secondBookingResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "id",
            CoreMatchers.notNullValue(),
            "bedroomId",
            CoreMatchers.is(bedroom.getId().toString()));

    final var storedBooking =
        r2dbcBookingRepository.findBookingById(secondBookingResponseModel.getId()).block();

    Assertions.assertNotNull(storedBooking);

    final var storedOperation = r2dbcOperationsRepository.findById(transactionReference).block();

    Assertions.assertNotNull(storedOperation);
    Assertions.assertEquals(Operation.BOOKING, storedOperation.getOperation());
  }

  @Test
  public void shouldCancelBooking() {

    final var bedroom = new Bedroom(UUID.randomUUID(), "001");

    r2dbcBedroomsRepository.save(bedroom).block();

    final var now = LocalDateTime.now();

    final var booking =
        new Booking(
            UUID.randomUUID(), new Customer(customerId), bedroom, new Period(now, now.plusDays(2)));

    r2dbcBookingRepository.save(booking).block();

    RestAssured.given()
        .delete("/booking/" + booking.getId().toString())
        .then()
        .statusCode(HttpStatus.OK.value());

    final var storedBooking = r2dbcBookingRepository.findBookingById(booking.getId()).block();

    Assertions.assertNull(storedBooking);
  }
}
