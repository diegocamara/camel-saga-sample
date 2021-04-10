package com.example.orders.application.web.route;

import com.example.orders.application.web.model.CreateOrderRequest;
import com.example.orders.application.web.model.OrderResponse;
import com.example.orders.domain.feature.CreateOrder;
import com.example.orders.domain.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrdersRoute extends RouteBuilder {

  private final CreateOrder createOrder;
  private final ObjectMapper objectMapper;

  @Override
  public void configure() throws Exception {
    rest("/orders")
        .post()
        .route()
        .process(
            exchange -> {
              final var createOrderRequest =
                  objectMapper.readValue(
                      exchange.getMessage().getBody(String.class), CreateOrderRequest.class);
              exchange.getMessage().setBody(createOrderRequest.toCreateOrderInput());
            })
        .bean(createOrder, "handle")
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.CREATED.value()))
        .bean(this, "orderResponse");
  }

  @SneakyThrows
  protected String orderResponse(Order order) {
    return objectMapper.writeValueAsString(new OrderResponse(order));
  }
}
