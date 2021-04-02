package com.example.orders.infrastructure.saga;

import com.example.orders.domain.model.Item;
import com.example.orders.domain.model.Order;
import com.example.orders.infrastructure.web.client.FlightWebClient;
import lombok.AllArgsConstructor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@AllArgsConstructor
public class OrderSaga extends RouteBuilder {

  public static final String CREATE_ORDER_ROUTE_ID = "create-order";
  public static final String BUY_FLIGHT_TICKET_ROUTE_ID = "buy-flight-ticket";
  public static final String CANCEL_FLIGHT_TICKET_ROUTE_ID = "cancel-flight-ticket";
  public static final String BOOKING_HOTEL_ROUTE_ID = "booking-hotel";
  public static final String CANCEL_BOOKING_HOTEL_ROUTE_ID = "cancel-booking-hotel";

  private final FlightWebClient flightWebClient;

  @Override
  public void configure() throws Exception {

    getContext().addService(new InMemorySagaService());

    final var CREATE_ORDER_ENDPOINT = "direct:" + CREATE_ORDER_ROUTE_ID;
    final var BUY_FLIGHT_TICKET_ENDPOINT = "direct:" + BUY_FLIGHT_TICKET_ROUTE_ID;
    final var CANCEL_FLIGHT_TICKET_ENDPOINT = "direct:" + CANCEL_FLIGHT_TICKET_ROUTE_ID;
    final var BOOKING_HOTEL_ENDPOINT = "direct:" + BOOKING_HOTEL_ROUTE_ID;
    final var CANCEL_BOOKING_HOTEL_ENDPOINT = "direct:" + CANCEL_BOOKING_HOTEL_ROUTE_ID;

    from(CREATE_ORDER_ENDPOINT)
        .id(CREATE_ORDER_ROUTE_ID)
        .saga()
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
        .bean(flightWebClient, "buyTicket(${body}, ${header.transactionId})")
        .option("buyTicketResponse", body())
        .log(" ******************* ${headers}");

    from(CANCEL_FLIGHT_TICKET_ENDPOINT)
        .transform(header("order"))
        .log("**************** ${headers}")
        .bean(flightWebClient, "cancelTicket(${body})");

    from(BOOKING_HOTEL_ENDPOINT)
        .id(BOOKING_HOTEL_ROUTE_ID)
        .saga()
        .propagation(SagaPropagation.MANDATORY)
        .compensation(CANCEL_BOOKING_HOTEL_ENDPOINT)
        .to("mock:testing");

    from(CANCEL_BOOKING_HOTEL_ENDPOINT).saga().to("mock:testing");
  }

  protected void cancelOrder(Order order) {}

  private Predicate containsItem(Item item) {
    return exchange -> {
      final var order = exchange.getMessage().getBody(Order.class);
      return order.getItems().contains(item);
    };
  }
}
