package com.example.flight;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.application.web.model.CancelTicketPurchaseRequest;
import com.example.flight.domain.model.Customer;
import com.example.flight.domain.model.Ticket;
import com.example.flight.domain.model.TicketCustomerRelationship;
import com.example.flight.infrasctructure.gateway.model.DebitRequest;
import com.example.flight.infrasctructure.gateway.model.DebitResponse;
import com.example.flight.infrasctructure.repository.table.Operation;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

class FlightApplicationTests extends IntegrationTest {

  public static final String TRANSACTION_REFERENCE_HEADER = "transaction-reference";
  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldCreateTicketCustomerRelationship() {

    mockPaymentsGatewayDebitCall();

    final var ticket = new Ticket();
    ticket.setId(UUID.randomUUID());
    ticket.setPrice(BigDecimal.TEN);
    ticket.setFrom("Location from");
    ticket.setDestination("Location destination");

    r2dbcEntityTemplateTicketsRepository.save(ticket).block();

    final var buyTicketRequest = new BuyTicketRequest();
    buyTicketRequest.setTicketId(ticket.getId());
    buyTicketRequest.setCustomerId(customerId);

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
            "ticket.id",
            CoreMatchers.is(ticket.getId().toString()),
            "ticket.from",
            CoreMatchers.is(ticket.getFrom()),
            "ticket.destination",
            CoreMatchers.is(ticket.getDestination()),
            "customer.id",
            CoreMatchers.is(customerId.toString()));

    final var buyTicketResponseModel =
        readValue(buyTicketResponse.body().print(), BuyTicketResponse.class);

    final var storedTicketCustomerRelationship =
        ticketsCustomerRelationshipRepository
            .findById(
                buyTicketResponseModel.getTicket().getId(),
                buyTicketResponseModel.getCustomer().getId())
            .block();

    Assertions.assertNotNull(storedTicketCustomerRelationship);
  }

  @Test
  void testBuyTicketIdempotency() {

    mockPaymentsGatewayDebitCall();

    final var ticket = new Ticket();
    ticket.setId(UUID.randomUUID());
    ticket.setPrice(BigDecimal.TEN);
    ticket.setFrom("Location from");
    ticket.setDestination("Location destination");

    r2dbcEntityTemplateTicketsRepository.save(ticket).block();

    final var buyTicketRequest = new BuyTicketRequest();
    buyTicketRequest.setTicketId(ticket.getId());
    buyTicketRequest.setCustomerId(customerId);

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
            "ticket.id",
            CoreMatchers.is(ticket.getId().toString()),
            "ticket.from",
            CoreMatchers.is(ticket.getFrom()),
            "ticket.destination",
            CoreMatchers.is(ticket.getDestination()),
            "customer.id",
            CoreMatchers.is(customerId.toString()));

    final var buyTicketResponseModel =
        readValue(firstBuyTicketResponse.body().print(), BuyTicketResponse.class);

    final var storedTicketCustomerRelationship =
        ticketsCustomerRelationshipRepository
            .findById(
                buyTicketResponseModel.getTicket().getId(),
                buyTicketResponseModel.getCustomer().getId())
            .block();

    Assertions.assertNotNull(storedTicketCustomerRelationship);

    final var secondBuyTicketResponse = buyTicketsRequestSpecification.post(url);

    secondBuyTicketResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "ticket.id",
            CoreMatchers.is(ticket.getId().toString()),
            "ticket.from",
            CoreMatchers.is(ticket.getFrom()),
            "ticket.destination",
            CoreMatchers.is(ticket.getDestination()),
            "customer.id",
            CoreMatchers.is(customerId.toString()));

    final var storedOperation =
        reactiveOperationsRepository.findById(UUID.fromString(transactionReference)).block();

    Assertions.assertNotNull(storedOperation);
    Assertions.assertEquals(Operation.BUY_TICKET, storedOperation.getOperation());
  }

  @Test
  public void shouldCancelTicketPurchase() {

    final var ticket = new Ticket();
    ticket.setId(UUID.randomUUID());
    ticket.setPrice(BigDecimal.TEN);
    ticket.setFrom("Location from");
    ticket.setDestination("Location destination");

    r2dbcEntityTemplateTicketsRepository.save(ticket).block();

    final var ticketCustomerRelationship =
        new TicketCustomerRelationship(ticket, new Customer(customerId));

    ticketsCustomerRelationshipRepository.save(ticketCustomerRelationship).block();

    final var cancelTicketPurchaseRequest = new CancelTicketPurchaseRequest();
    cancelTicketPurchaseRequest.setTicketId(ticket.getId());
    cancelTicketPurchaseRequest.setCustomerId(customerId);

    final var cancelTicketPurchaseRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(writeValueAsString(cancelTicketPurchaseRequest));

    final var cancelTicketPurchaseResponse =
        cancelTicketPurchaseRequestSpecification.delete("/tickets");

    cancelTicketPurchaseResponse.then().statusCode(HttpStatus.OK.value());

    final var storedTicketCustomerRelationship =
        ticketsCustomerRelationshipRepository.findById(ticket.getId(), customerId).block();

    Assertions.assertNull(storedTicketCustomerRelationship);
  }

  private void mockPaymentsGatewayDebitCall() {
    final var debitRequest = new DebitRequest();
    debitRequest.setAmount(BigDecimal.TEN);

    final var paymentDebitRequest =
        HttpRequest.request()
            .withPath("/accounts/" + customerId.toString() + "/debit")
            .withMethod("PATCH")
            .withBody(writeValueAsString(debitRequest));

    final var debitResponse = new DebitResponse();
    debitResponse.setCustomer(customerId);
    debitResponse.setUsed(BigDecimal.TEN);
    debitResponse.setTransactionId(UUID.randomUUID());

    final var paymentDebitResponse =
        HttpResponse.response()
            .withStatusCode(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(writeValueAsString(debitResponse));

    clientAndServer.when(paymentDebitRequest).respond(paymentDebitResponse);
  }
}
