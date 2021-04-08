package com.example.orders.infrastructure.saga;

import com.example.orders.domain.model.Order;
import com.example.orders.infrastructure.repository.springdata.document.BookingResponseDocument;
import com.example.orders.infrastructure.repository.springdata.document.BuyTicketResponseDocument;
import com.example.orders.infrastructure.repository.springdata.document.OrderDocument;
import com.example.orders.infrastructure.repository.springdata.repository.SpringDataOrdersRepository;
import com.example.orders.infrastructure.web.client.FlightWebClient;
import com.example.orders.infrastructure.web.client.HotelWebClient;
import com.example.orders.infrastructure.web.model.*;
import lombok.AllArgsConstructor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OrderSaga extends RouteBuilder {

  public static final String CREATE_ORDER_ROUTE_ID = "create-order";
  public static final String BUY_FLIGHT_TICKET_ROUTE_ID = "buy-flight-ticket";
  public static final String CANCEL_FLIGHT_TICKET_ROUTE_ID = "cancel-flight-ticket";
  public static final String BOOKING_HOTEL_ROUTE_ID = "booking-hotel";
  public static final String CANCEL_BOOKING_HOTEL_ROUTE_ID = "cancel-booking-hotel";
  public static final String COMPLETE_ORDER_ROUTE_ID = "complete-order";
  public static final String OPERATION_REFERENCE_HEADER = "operationReference";

  private final FlightWebClient flightWebClient;
  private final HotelWebClient hotelWebClient;
  private final SpringDataOrdersRepository springDataOrdersRepository;
  private final CamelSagaService camelSagaService;

  @Override
  public void configure() throws Exception {
    getContext().addService(camelSagaService);
    //    getContext().addService(new InMemorySagaService());

    final var DIRECT_PREFIX = "direct:";
    final var CREATE_ORDER_ENDPOINT = DIRECT_PREFIX + CREATE_ORDER_ROUTE_ID;
    final var BUY_FLIGHT_TICKET_ENDPOINT = DIRECT_PREFIX + BUY_FLIGHT_TICKET_ROUTE_ID;
    final var CANCEL_FLIGHT_TICKET_ENDPOINT = DIRECT_PREFIX + CANCEL_FLIGHT_TICKET_ROUTE_ID;
    final var BOOKING_HOTEL_ENDPOINT = DIRECT_PREFIX + BOOKING_HOTEL_ROUTE_ID;
    final var CANCEL_BOOKING_HOTEL_ENDPOINT = DIRECT_PREFIX + CANCEL_BOOKING_HOTEL_ROUTE_ID;

    from(CREATE_ORDER_ENDPOINT)
        .id(CREATE_ORDER_ROUTE_ID)
        .saga()
        .setHeader(OPERATION_REFERENCE_HEADER, simple(UUID.randomUUID().toString()))
        .timeout(Duration.ofMinutes(1))
        .setProperty("order", body())
        .choice()
        .when(containsFlightTicketPurchase())
        .to(BUY_FLIGHT_TICKET_ENDPOINT)
        .choice()
        .when(containsHotelBooking())
        .to(BOOKING_HOTEL_ENDPOINT)
        .end();

    from(BUY_FLIGHT_TICKET_ENDPOINT)
        .id(BUY_FLIGHT_TICKET_ROUTE_ID)
        .saga()
        .propagation(SagaPropagation.MANDATORY)
        .compensation(CANCEL_FLIGHT_TICKET_ENDPOINT)
        .option("order", body())
        .option(OPERATION_REFERENCE_HEADER, header(OPERATION_REFERENCE_HEADER))
        .bean(this, "buyTicketRequest")
        .bean(flightWebClient, "buyTicket(${body}, ${header.operationReference})")
        .bean(this, "updateOrderForBuyTicket(${exchangeProperty.order}, ${body})")
        .end();

    from(CANCEL_FLIGHT_TICKET_ENDPOINT)
        .id(CANCEL_FLIGHT_TICKET_ROUTE_ID)
        .log("************** ${headers}")
        .transform(header(OPERATION_REFERENCE_HEADER))
        .bean(flightWebClient, "cancelTicket")
        .end();

    from(BOOKING_HOTEL_ENDPOINT)
        .id(BOOKING_HOTEL_ROUTE_ID)
        .transform(exchangeProperty("order"))
        .saga()
        .propagation(SagaPropagation.MANDATORY)
        .compensation(CANCEL_BOOKING_HOTEL_ENDPOINT)
        .option("order", body())
        .option(OPERATION_REFERENCE_HEADER, header(OPERATION_REFERENCE_HEADER))
        .bean(this, "bookingRequest")
        .bean(hotelWebClient, "createBooking(${body}, ${headers.operationReference})")
        .bean(this, "updateOrderForBooking(${exchangeProperty.order}, ${body})");

    from(CANCEL_BOOKING_HOTEL_ENDPOINT)
        .id(CANCEL_BOOKING_HOTEL_ROUTE_ID)
        .transform(header(OPERATION_REFERENCE_HEADER))
        .bean(hotelWebClient, "cancelBooking");
  }

  protected BuyTicketRequest buyTicketRequest(Order order) {
    final var ticketId = order.getItems().getFlightTicketPurchase().getTicketId();
    final var customerId = order.getCustomer().getId();
    return new BuyTicketRequest(ticketId, customerId);
  }

  protected CancelTicketPurchaseRequest cancelTicketPurchaseRequest(Order order) {

    final var orderDocument = springDataOrdersRepository.findById(order.getId()).orElseThrow();

    final var ticketId =
        orderDocument
            .getTimeline()
            .buyTicketResponseDocument()
            .map(BuyTicketResponseDocument::getTicketId)
            .orElseThrow();

    return new CancelTicketPurchaseRequest(ticketId, order.getCustomer().getId());
  }

  protected BookingRequest bookingRequest(Order order) {
    final var hotelBooking = order.getItems().getHotelBooking();
    final var customerId = order.getCustomer().getId();
    final var bedroomId = hotelBooking.getBedroomId();
    final var from = hotelBooking.getFrom();
    final var to = hotelBooking.getTo();
    return new BookingRequest(bedroomId, customerId, from, to);
  }

  protected OrderDocument orderDocument(Order order) {
    return new OrderDocument(order);
  }

  protected void updateOrderForBuyTicket(Order order, BuyTicketResponse buyTicketResponse) {
    final var storedOrderOptional = springDataOrdersRepository.findById(order.getId());
    storedOrderOptional.ifPresent(
        orderDocument -> {
          orderDocument
              .getTimeline()
              .getEvents()
              .add(new BuyTicketResponseDocument(buyTicketResponse));
          springDataOrdersRepository.save(orderDocument);
        });
  }

  protected void updateOrderForBooking(Order order, BookingResponse bookingResponse) {
    final var storedOrderOptional = springDataOrdersRepository.findById(order.getId());
    storedOrderOptional.ifPresent(
        orderDocument -> {
          orderDocument.getTimeline().getEvents().add(new BookingResponseDocument(bookingResponse));
          springDataOrdersRepository.save(orderDocument);
        });
  }

  private Predicate containsFlightTicketPurchase() {
    return exchange -> {
      final var order = exchange.getProperty("order", Order.class);
      return order.getItems().flightTicketPurchaseExists();
    };
  }

  private Predicate containsHotelBooking() {
    return exchange -> {
      final var order = exchange.getProperty("order", Order.class);
      return order.getItems().hotelBookingExists();
    };
  }
}
