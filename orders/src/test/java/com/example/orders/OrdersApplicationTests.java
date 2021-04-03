package com.example.orders;

import com.example.orders.application.web.model.CreateOrderRequest;
import com.example.orders.application.web.model.OrderResponse;
import com.example.orders.domain.model.Item;
import com.example.orders.infrastructure.repository.springdata.document.BookingResponseDocument;
import com.example.orders.infrastructure.repository.springdata.document.BuyTicketResponseDocument;
import com.example.orders.infrastructure.web.model.BookingResponse;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;
import com.example.orders.infrastructure.web.model.CustomerWebModel;
import com.example.orders.infrastructure.web.model.TicketWebModel;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class OrdersApplicationTests extends IntegrationTest {

  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldCreateCustomerOrder() {

    mockBuyTicketSuccess();
    mockBookingRequestSuccess();

    final var createOrderRequest = new CreateOrderRequest();
    createOrderRequest.setCustomerId(customerId);
    createOrderRequest.setItems(Arrays.asList(Item.BUY_FLIGHT_TICKET, Item.BOOKING_HOTEL));

    final var createOrderRequestSpecification =
        RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(writeValueAsString(createOrderRequest));

    final var createOrderResponse = createOrderRequestSpecification.post("/orders");

    final var orderResponse = readValue(createOrderResponse.body().print(), OrderResponse.class);

    verify(1, postRequestedFor(urlEqualTo("/tickets")));
    verify(1, postRequestedFor(urlEqualTo("/booking")));
    verify(0, deleteRequestedFor(urlEqualTo("/tickets")));
    verify(0, deleteRequestedFor(urlEqualTo("/booking")));

    final var storedOrderOptional = springDataOrdersRepository.findById(orderResponse.getId());

    Assertions.assertTrue(storedOrderOptional.isPresent());

    final var storedOrder = storedOrderOptional.get();

    Assertions.assertEquals(2, storedOrder.getTimeline().getEvents().size());

    final var buyTicketResponseDocument =
        storedOrder.getTimeline().getEvents().stream()
            .filter(
                eventDocument ->
                    BuyTicketResponseDocument.class.isAssignableFrom(eventDocument.getClass()))
            .map(eventDocument -> (BuyTicketResponseDocument) eventDocument)
            .findFirst()
            .orElseThrow();

    Assertions.assertNotNull(buyTicketResponseDocument.getTicketId());

    final var bookingResponseDocument =
        storedOrder.getTimeline().getEvents().stream()
            .filter(
                eventDocument ->
                    BookingResponseDocument.class.isAssignableFrom(eventDocument.getClass()))
            .map(eventDocument -> (BookingResponseDocument) eventDocument)
            .findFirst()
            .orElseThrow();

    Assertions.assertNotNull(bookingResponseDocument.getBookingId());
  }

  private void mockBuyTicketSuccess() {

    final var ticketId = UUID.randomUUID();

    final var buyTicketResponse = new BuyTicketResponse();
    final var ticket = new TicketWebModel();
    ticket.setId(ticketId);
    ticket.setFrom("From");
    ticket.setDestination("Destination");
    ticket.setPrice(BigDecimal.TEN);
    buyTicketResponse.setTicket(ticket);
    final var customer = new CustomerWebModel();
    customer.setId(customerId);
    buyTicketResponse.setCustomer(customer);

    stubFor(
        post(urlEqualTo("/tickets"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.CREATED.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(writeValueAsString(buyTicketResponse))));
  }

  private void mockBookingRequestSuccess() {

    final var bookingId = UUID.randomUUID();
    final var bedroomId = UUID.randomUUID();

    final var bookingResponse = new BookingResponse();
    bookingResponse.setId(bookingId);
    bookingResponse.setBedroomId(bedroomId);

    stubFor(
        post(urlEqualTo("/booking"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.CREATED.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(writeValueAsString(bookingResponse))));
  }
}
