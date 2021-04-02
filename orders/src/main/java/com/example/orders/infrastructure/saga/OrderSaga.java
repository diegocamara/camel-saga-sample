package com.example.orders.infrastructure.saga;

import com.example.orders.domain.model.Item;
import com.example.orders.domain.model.Order;
import com.example.orders.infrastructure.repository.springdata.document.OrderDocument;
import com.example.orders.infrastructure.repository.springdata.repository.SpringDataOrdersRepository;
import com.example.orders.infrastructure.web.client.FlightWebClient;
import com.example.orders.infrastructure.web.client.HotelWebClient;
import com.example.orders.infrastructure.web.model.BookingRequest;
import com.example.orders.infrastructure.web.model.BuyTicketRequest;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;
import com.example.orders.infrastructure.web.model.CancelTicketPurchaseRequest;
import lombok.AllArgsConstructor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
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

  private final FlightWebClient flightWebClient;
  private final HotelWebClient hotelWebClient;
  private final SpringDataOrdersRepository springDataOrdersRepository;

  @Override
  public void configure() throws Exception {

    getContext().addService(new InMemorySagaService());

    final var DIRECT_PREFIX = "direct:";
    final var CREATE_ORDER_ENDPOINT = DIRECT_PREFIX + CREATE_ORDER_ROUTE_ID;
    final var BUY_FLIGHT_TICKET_ENDPOINT = DIRECT_PREFIX + BUY_FLIGHT_TICKET_ROUTE_ID;
    final var CANCEL_FLIGHT_TICKET_ENDPOINT = DIRECT_PREFIX + CANCEL_FLIGHT_TICKET_ROUTE_ID;
    final var BOOKING_HOTEL_ENDPOINT = DIRECT_PREFIX + BOOKING_HOTEL_ROUTE_ID;
    final var CANCEL_BOOKING_HOTEL_ENDPOINT = DIRECT_PREFIX + CANCEL_BOOKING_HOTEL_ROUTE_ID;
    final var COMPLETE_ORDER_ENDPOINT = DIRECT_PREFIX + COMPLETE_ORDER_ROUTE_ID;

    from(CREATE_ORDER_ENDPOINT)
        .id(CREATE_ORDER_ROUTE_ID)
        .saga()
        .option("order", body())
        .completion(COMPLETE_ORDER_ENDPOINT)
        .timeout(Duration.ofMinutes(1))
        .setProperty("order", body())
        .choice()
        .when(containsItem(Item.BUY_FLIGHT_TICKET))
        .to(BUY_FLIGHT_TICKET_ENDPOINT)
        .setBody(exchangeProperty("order"))
        .choice()
        .when(containsItem(Item.BOOKING_HOTEL))
        .to(BOOKING_HOTEL_ENDPOINT)
        .end();

    from(BUY_FLIGHT_TICKET_ENDPOINT)
        .id(BUY_FLIGHT_TICKET_ROUTE_ID)
        .saga()
        .propagation(SagaPropagation.MANDATORY)
        .option("order", body())
        .compensation(CANCEL_FLIGHT_TICKET_ENDPOINT)
        .bean(this, "buyTicketRequest")
        .bean(flightWebClient, "buyTicket(${body}, ${header.transactionId})")
        .option("buyTicketResponse", body());

    from(CANCEL_FLIGHT_TICKET_ENDPOINT)
        .transform(header("buyTicketResponse"))
        .bean(this, "cancelTicketPurchaseRequest")
        .bean(flightWebClient, "cancelTicket");

    from(BOOKING_HOTEL_ENDPOINT)
        .id(BOOKING_HOTEL_ROUTE_ID)
        .saga()
        .propagation(SagaPropagation.MANDATORY)
        .compensation(CANCEL_BOOKING_HOTEL_ENDPOINT)
        .bean(this, "bookingRequest")
        .bean(hotelWebClient, "createBooking(${body})")
        .option("bookingResponse", body());

    from(CANCEL_BOOKING_HOTEL_ENDPOINT)
        .transform(simple("${header.bookingResponse.id}"))
        .bean(hotelWebClient, "cancelBooking");

    from(COMPLETE_ORDER_ENDPOINT)
        .id(COMPLETE_ORDER_ROUTE_ID)
        .transform(header("order"))
        .bean(this, "orderDocument")
        .bean(this, "saveOrderDocument");
  }

  protected void saveOrderDocument(OrderDocument orderDocument) {
    springDataOrdersRepository.save(orderDocument);
    orderDocument.getCustomer();
  }

  protected BuyTicketRequest buyTicketRequest(Order order) {
    final var ticketId = UUID.randomUUID();
    final var customerId = order.getCustomer().getId();
    return new BuyTicketRequest(ticketId, customerId);
  }

  protected CancelTicketPurchaseRequest cancelTicketPurchaseRequest(
      BuyTicketResponse buyTicketResponse) {
    final var ticketId = buyTicketResponse.getTicket().getId();
    final var customerId = buyTicketResponse.getCustomer().getId();
    return new CancelTicketPurchaseRequest(ticketId, customerId);
  }

  protected BookingRequest bookingRequest(Order order) {
    final var bedroomId = UUID.randomUUID();
    final var customerId = order.getCustomer().getId();
    final var from = LocalDateTime.now();
    final var to = from.plusDays(2);
    return new BookingRequest(bedroomId, customerId, from, to);
  }

  protected OrderDocument orderDocument(Order order) {
    return new OrderDocument(order);
  }

  private Predicate containsItem(Item item) {
    return exchange -> {
      final var order = exchange.getMessage().getBody(Order.class);
      return order.getItems().contains(item);
    };
  }
}