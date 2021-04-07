package com.example.flight;

import com.example.flight.application.web.controller.TicketsController;
import com.example.flight.application.web.controller.transaction.buyticket.BuyTicketOperation;
import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.domain.model.Customer;
import com.example.flight.domain.model.Ticket;
import com.example.flight.domain.model.TicketCustomerRelationship;
import com.example.flight.infrastructure.gateway.model.CreditRequest;
import com.example.flight.infrastructure.gateway.model.CreditResponse;
import com.example.flight.infrastructure.gateway.model.DebitRequest;
import com.example.flight.infrastructure.gateway.model.DebitResponse;
import com.example.flight.infrastructure.operation.Status;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class FlightApplicationTests extends IntegrationTest {

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
                TicketsController.OPERATION_REFERENCE_HEADER,
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

    final var operationReference = UUID.randomUUID().toString();

    final var buyTicketsRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                TicketsController.OPERATION_REFERENCE_HEADER,
                operationReference)
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

    final var storedOperation =
        reactiveOperationsRepository.findById(UUID.fromString(operationReference)).block();

    final var storedOperationBuyTicketResponse =
        readValue(
            Objects.requireNonNull(storedOperation).getOutput().asString(),
            BuyTicketResponse.class);

    Assertions.assertNotNull(storedOperation);
    Assertions.assertNotNull(storedOperationBuyTicketResponse);
    Assertions.assertEquals(buyTicketResponseModel, storedOperationBuyTicketResponse);
  }

  @Test
  public void shouldCancelTicketPurchase() {

    mockPaymentsGatewayCreditCall();

    final var ticket = new Ticket();
    ticket.setId(UUID.randomUUID());
    ticket.setPrice(BigDecimal.TEN);
    ticket.setFrom("Location from");
    ticket.setDestination("Location destination");

    r2dbcEntityTemplateTicketsRepository.save(ticket).block();

    final var ticketCustomerRelationship =
        new TicketCustomerRelationship(ticket, new Customer(customerId));

    ticketsCustomerRelationshipRepository.save(ticketCustomerRelationship).block();

    final var buyTicketResponse = new BuyTicketResponse(ticketCustomerRelationship);

    final var operationReference = UUID.randomUUID();

    final var buyTicketOperation = new BuyTicketOperation(operationReference, buyTicketResponse);
    buyTicketOperation.setStatus(Status.EXECUTED);
    buyTicketOperationsRepository.save(buyTicketOperation).block();

    final var cancelTicketPurchaseRequestSpecification =
        RestAssured.given()
            .headers(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE,
                TicketsController.OPERATION_REFERENCE_HEADER,
                operationReference);

    final var cancelTicketPurchaseResponse =
        cancelTicketPurchaseRequestSpecification.delete("/tickets");

    cancelTicketPurchaseResponse.then().statusCode(HttpStatus.OK.value());

    final var storedTicketCustomerRelationship =
        ticketsCustomerRelationshipRepository.findById(ticket.getId(), customerId).block();

    Assertions.assertNull(storedTicketCustomerRelationship);

    final var storedBuyTicketOperation =
        buyTicketOperationsRepository.findByOperationReference(operationReference).block();

    Assertions.assertNotNull(storedBuyTicketOperation);
    Assertions.assertEquals(buyTicketResponse, storedBuyTicketOperation.getOutput());
    Assertions.assertTrue(storedBuyTicketOperation.isRollback());
  }

  private void mockPaymentsGatewayDebitCall() {

    final var debitRequest = new DebitRequest(BigDecimal.TEN);

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
