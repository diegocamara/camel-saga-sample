package com.example.orders;

import com.example.orders.application.web.model.CreateOrderRequest;
import com.example.orders.application.web.model.FlightTicketPurchaseWebModel;
import com.example.orders.application.web.model.HotelBookingWebModel;
import com.example.orders.application.web.model.OrderResponse;
import com.example.orders.infrastructure.repository.springdata.document.BookingResponseDocument;
import com.example.orders.infrastructure.repository.springdata.document.BuyTicketResponseDocument;
import com.example.orders.infrastructure.web.model.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class OrdersApplicationTests extends IntegrationTest {

  public static final String OPERATION_REFERENCE_HEADER = "operation-reference";
  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldCreateCustomerOrder() {

    final var ticketId = UUID.randomUUID();
    final var bedroomId = UUID.randomUUID();

    final var createOrderRequest = new CreateOrderRequest();
    createOrderRequest.setCustomerId(customerId);

    final var flightTicketPurchaseWebModel = new FlightTicketPurchaseWebModel();
    flightTicketPurchaseWebModel.setTicketId(ticketId);
    createOrderRequest.getItems().setFlightTicketPurchaseWebModel(flightTicketPurchaseWebModel);

    final var hotelBookingWebModel = new HotelBookingWebModel();
    hotelBookingWebModel.setBedroomId(bedroomId);
    final var now = LocalDateTime.now();
    hotelBookingWebModel.setFrom(now);
    hotelBookingWebModel.setTo(now.plusDays(2));
    createOrderRequest.getItems().setHotelBookingWebModel(hotelBookingWebModel);

    final var createOrderRequestSpecification =
        RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(writeValueAsString(createOrderRequest));

    final var buyTicketRequest = new BuyTicketRequest(ticketId, customerId);

    mockBuyTicketSuccess(buyTicketRequest);

    final var bookingRequest =
        new BookingRequest(
            bedroomId, customerId, hotelBookingWebModel.getFrom(), hotelBookingWebModel.getTo());

    mockBookingRequestSuccess(bookingRequest);

    final var createOrderResponse = createOrderRequestSpecification.post("/orders");

    final var orderResponse = readValue(createOrderResponse.body().print(), OrderResponse.class);

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

    verify(
        1,
        postRequestedFor(urlEqualTo("/tickets"))
            .withHeader(
                OPERATION_REFERENCE_HEADER,
                equalTo(buyTicketResponseDocument.getOperationReference().toString()))
            .withRequestBody(equalToJson(writeValueAsString(buyTicketRequest))));
    verify(
        1,
        postRequestedFor(urlEqualTo("/booking"))
            .withHeader(
                OPERATION_REFERENCE_HEADER,
                equalTo(bookingResponseDocument.getTransactionId().toString()))
            .withRequestBody(equalToJson(writeValueAsString(bookingRequest))));
    verify(0, deleteRequestedFor(urlEqualTo("/tickets")));
    verify(0, deleteRequestedFor(urlEqualTo("/booking")));
  }

  @Test
  void shouldRollbackCustomerOrder() {

    final var ticketId = UUID.randomUUID();
    final var bedroomId = UUID.randomUUID();

    final var createOrderRequest = new CreateOrderRequest();
    createOrderRequest.setCustomerId(customerId);

    final var flightTicketPurchaseWebModel = new FlightTicketPurchaseWebModel();
    flightTicketPurchaseWebModel.setTicketId(ticketId);
    createOrderRequest.getItems().setFlightTicketPurchaseWebModel(flightTicketPurchaseWebModel);

    final var hotelBookingWebModel = new HotelBookingWebModel();
    hotelBookingWebModel.setBedroomId(bedroomId);
    final var now = LocalDateTime.now();
    hotelBookingWebModel.setFrom(now);
    hotelBookingWebModel.setTo(now.plusDays(2));
    createOrderRequest.getItems().setHotelBookingWebModel(hotelBookingWebModel);

    final var createOrderRequestSpecification =
        RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(writeValueAsString(createOrderRequest));

    final var buyTicketRequest = new BuyTicketRequest(ticketId, customerId);

    mockBuyTicketSuccess(buyTicketRequest);

    final var bookingRequest =
        new BookingRequest(
            bedroomId, customerId, hotelBookingWebModel.getFrom(), hotelBookingWebModel.getTo());

    mockBookingRequestFail(bookingRequest);

    mockCancelTicketPurchaseRequestSuccess();

    mockCancelBookingRequestSuccess();

    final var createOrderResponse = createOrderRequestSpecification.post("/orders");

    Assertions.assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR.value(), createOrderResponse.statusCode());

    verify(
        1,
        postRequestedFor(urlEqualTo("/tickets"))
            .withHeader(OPERATION_REFERENCE_HEADER, matching(".*"))
            .withRequestBody(equalToJson(writeValueAsString(buyTicketRequest))));
    verify(
        1,
        postRequestedFor(urlEqualTo("/booking"))
            .withHeader(OPERATION_REFERENCE_HEADER, matching(".*"))
            .withRequestBody(equalToJson(writeValueAsString(bookingRequest))));
    verify(
        1,
        deleteRequestedFor(urlEqualTo("/tickets"))
            .withHeader(OPERATION_REFERENCE_HEADER, matching(".*")));
    verify(1, deleteRequestedFor(urlEqualTo("/booking")));
  }

  private void mockCancelTicketPurchaseRequestSuccess() {
    stubFor(
        delete(urlEqualTo("/tickets"))
            .withHeader(OPERATION_REFERENCE_HEADER, matching(".*"))
            .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
  }

  private void mockCancelBookingRequestSuccess() {
    stubFor(
        delete(urlEqualTo("/booking"))
            .withHeader(OPERATION_REFERENCE_HEADER, matching(".*"))
            .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
  }

  private void mockBuyTicketSuccess(BuyTicketRequest buyTicketRequest) {

    final var buyTicketResponse = new BuyTicketResponse();
    final var ticket = new TicketWebModel();
    ticket.setId(buyTicketRequest.getTicketId());
    ticket.setFrom("From");
    ticket.setDestination("Destination");
    ticket.setPrice(BigDecimal.TEN);
    buyTicketResponse.setTicket(ticket);
    final var customer = new CustomerWebModel();
    customer.setId(customerId);
    buyTicketResponse.setCustomer(customer);

    stubFor(
        post(urlEqualTo("/tickets"))
            .withRequestBody(equalToJson(writeValueAsString(buyTicketRequest)))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.CREATED.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(writeValueAsString(buyTicketResponse))));
  }

  private void mockBookingRequestSuccess(BookingRequest bookingRequest) {

    final var bookingId = UUID.randomUUID();

    final var bookingResponse = new BookingResponse();
    bookingResponse.setId(bookingId);
    bookingResponse.setBedroomId(bookingRequest.getBedroomId());

    stubFor(
        post(urlEqualTo("/booking"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.CREATED.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(writeValueAsString(bookingResponse))));
  }

  private void mockBookingRequestFail(BookingRequest bookingRequest) {

    final var bookingId = UUID.randomUUID();

    final var bookingResponse = new BookingResponse();
    bookingResponse.setId(bookingId);
    bookingResponse.setBedroomId(bookingRequest.getBedroomId());

    stubFor(
        post(urlEqualTo("/booking"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.CONFLICT.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
  }
}
