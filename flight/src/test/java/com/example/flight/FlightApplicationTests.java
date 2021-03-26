package com.example.flight;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.TicketResponse;
import com.example.flight.infrasctructure.repository.table.Operation;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

class FlightApplicationTests extends IntegrationTest {

  public static final String TRANSACTION_REFERENCE_HEADER = "transaction-reference";
  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldCreateCustomerTicket() {

    final var buyTicketRequest = new BuyTicketRequest();
    buyTicketRequest.setCustomer(customerId);
    buyTicketRequest.setFrom("Location from");
    buyTicketRequest.setDestination("Location destination");

    final var transactionReference = UUID.randomUUID().toString();

    final var buyTicketsRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                TRANSACTION_REFERENCE_HEADER,
                transactionReference)
            .body(writeValueAsString(buyTicketRequest));

    final var url = "/tickets";

    final var buyTicketResponse = buyTicketsRequestSpecification.post(url);

    buyTicketResponse
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body(
            "id",
            CoreMatchers.notNullValue(),
            "customer",
            CoreMatchers.is(customerId.toString()),
            "from",
            CoreMatchers.is(buyTicketRequest.getFrom()),
            "destination",
            CoreMatchers.is(buyTicketRequest.getDestination()));

    final var ticketResponse = readValue(buyTicketResponse.body().print(), TicketResponse.class);

    final var storedTicket = reactiveTicketsRepository.findById(ticketResponse.getId()).block();

    Assertions.assertNotNull(storedTicket);
    Assertions.assertEquals(buyTicketRequest.getFrom(), storedTicket.getFrom());
    Assertions.assertEquals(buyTicketRequest.getDestination(), storedTicket.getDestination());
    Assertions.assertEquals(customerId, storedTicket.getCustomerId());
  }

  @Test
  void testBuyTicketIdempotency() {

    final var buyTicketRequest = new BuyTicketRequest();
    buyTicketRequest.setCustomer(customerId);
    buyTicketRequest.setFrom("Location from");
    buyTicketRequest.setDestination("Location destination");

    final var transactionReference = UUID.randomUUID().toString();

    final var buyTicketsRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                TRANSACTION_REFERENCE_HEADER,
                transactionReference)
            .body(writeValueAsString(buyTicketRequest));

    final var url = "/tickets";

    final var firstBuyTicketResponse = buyTicketsRequestSpecification.post(url);

    firstBuyTicketResponse
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body(
            "id",
            CoreMatchers.notNullValue(),
            "customer",
            CoreMatchers.is(customerId.toString()),
            "from",
            CoreMatchers.is(buyTicketRequest.getFrom()),
            "destination",
            CoreMatchers.is(buyTicketRequest.getDestination()));

    final var ticketResponse =
        readValue(firstBuyTicketResponse.body().print(), TicketResponse.class);

    final var storedTicket = reactiveTicketsRepository.findById(ticketResponse.getId()).block();

    Assertions.assertNotNull(storedTicket);
    Assertions.assertEquals(buyTicketRequest.getFrom(), storedTicket.getFrom());
    Assertions.assertEquals(buyTicketRequest.getDestination(), storedTicket.getDestination());
    Assertions.assertEquals(customerId, storedTicket.getCustomerId());

    final var secondBuyTicketResponse = buyTicketsRequestSpecification.post(url);

    secondBuyTicketResponse.then().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

    final var storedOperation =
        reactiveOperationsRepository.findById(UUID.fromString(transactionReference)).block();

    Assertions.assertNotNull(storedOperation);
    Assertions.assertEquals(Operation.BUY_TICKET, storedOperation.getOperation());
  }
}
