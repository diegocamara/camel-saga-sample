package com.example.hotel;

import com.example.hotel.application.web.controller.BookingController;
import com.example.hotel.application.web.controller.transaction.booking.BookingOperation;
import com.example.hotel.application.web.model.BookingRequest;
import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.domain.model.Bedroom;
import com.example.hotel.domain.model.Booking;
import com.example.hotel.domain.model.Customer;
import com.example.hotel.domain.model.Period;
import com.example.hotel.infrasctructure.gateway.model.CreditRequest;
import com.example.hotel.infrasctructure.gateway.model.CreditResponse;
import com.example.hotel.infrasctructure.gateway.model.DebitRequest;
import com.example.hotel.infrasctructure.gateway.model.DebitResponse;
import com.example.hotel.infrasctructure.operation.Status;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class HotelApplicationTests extends IntegrationTest {

  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldCreateBooking() {

    mockPaymentsGatewayDebitCall();

    final var bedroom = new Bedroom(UUID.randomUUID(), "001", BigDecimal.TEN);

    r2dbcBedroomsRepository.save(bedroom).block();

    final var now = LocalDateTime.now();

    final var bookingRequest = new BookingRequest();
    bookingRequest.setCustomerId(customerId);
    bookingRequest.setBedroomId(bedroom.getId());
    bookingRequest.setFrom(now);
    bookingRequest.setTo(now.plusDays(2));

    final var operationReference = UUID.randomUUID();

    final var bookingRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                BookingController.OPERATION_REFERENCE_HEADER,
                operationReference)
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

    mockPaymentsGatewayDebitCall();

    final var bedroom = new Bedroom(UUID.randomUUID(), "001", BigDecimal.TEN);

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
                BookingController.OPERATION_REFERENCE_HEADER,
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
        .statusCode(HttpStatus.CREATED.value())
        .body(
            "id",
            CoreMatchers.notNullValue(),
            "bedroomId",
            CoreMatchers.is(bedroom.getId().toString()));

    final var storedBooking =
        r2dbcBookingRepository.findBookingById(secondBookingResponseModel.getId()).block();

    Assertions.assertNotNull(storedBooking);

    final var storedOperation =
        r2dbcOperationsRepository.findByOperationReference(transactionReference).block();

    Assertions.assertNotNull(storedOperation);
    Assertions.assertTrue(storedOperation.isExecuted());
  }

  @Test
  public void shouldCancelBooking() {

    mockPaymentsGatewayCreditCall();

    final var bedroom = new Bedroom(UUID.randomUUID(), "001", BigDecimal.TEN);

    r2dbcBedroomsRepository.save(bedroom).block();

    final var now = LocalDateTime.now();

    final var booking =
        new Booking(
            UUID.randomUUID(), new Customer(customerId), bedroom, new Period(now, now.plusDays(2)));

    r2dbcBookingRepository.save(booking).block();

    final var operationReference = UUID.randomUUID();

    final var bookingResponse = new BookingResponse(booking);

    final var bookingOperation = new BookingOperation(operationReference, bookingResponse);
    bookingOperation.setStatus(Status.EXECUTED);

    r2dbcOperationsRepository.save(bookingOperation).block();

    RestAssured.given()
        .header(BookingController.OPERATION_REFERENCE_HEADER, operationReference)
        .delete("/booking")
        .then()
        .statusCode(HttpStatus.OK.value());

    final var storedBooking = r2dbcBookingRepository.findBookingById(booking.getId()).block();

    Assertions.assertNull(storedBooking);

    final var storedBookingOperation =
        r2dbcOperationsRepository.findByOperationReference(operationReference).block();

    Assertions.assertNotNull(storedBookingOperation);
    Assertions.assertTrue(storedBookingOperation.isRollback());
  }

  private void mockPaymentsGatewayDebitCall() {

    final var debitRequest = new DebitRequest();
    debitRequest.setAmount(BigDecimal.TEN);

    final var debitResponse = new DebitResponse();
    debitResponse.setCustomer(customerId);
    debitResponse.setUsed(BigDecimal.TEN);
    debitResponse.setTransactionId(UUID.randomUUID());

    stubFor(
        patch(urlEqualTo("/accounts/" + customerId.toString() + "/debit"))
            .withRequestBody(equalToJson(writeValueAsString(debitRequest)))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(writeValueAsString(debitResponse))));
  }

  private void mockPaymentsGatewayCreditCall() {

    final var creditRequest = new CreditRequest(BigDecimal.TEN);
    creditRequest.setAmount(BigDecimal.TEN);

    final var creditResponse = new CreditResponse();
    creditResponse.setCustomer(customerId);
    creditResponse.setUsed(BigDecimal.TEN);
    creditResponse.setTransactionId(UUID.randomUUID());

    stubFor(
        patch(urlEqualTo("/accounts/" + customerId.toString() + "/credit"))
            .withRequestBody(equalToJson(writeValueAsString(creditRequest)))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(writeValueAsString(creditResponse))));
  }
}
