package com.example.hotel.infrasctructure.configuration;

import com.example.hotel.application.messaging.model.BookingRequestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

// @Configuration
public class RabbitMQConfiguration {

  @Autowired private ObjectMapper objectMapper;

  @Value("${queue.booking-request}")
  private String bookingRequestQueue;

  @Bean("bookingRequestMessageFlux")
  public Flux<BookingRequestMessage> bookingRequestMessageFlux(
      ConnectionFactory connectionFactory) {

    final var messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
    messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
    messageListenerContainer.addQueueNames(bookingRequestQueue);
    messageListenerContainer.setDeclarationRetries(2);
    messageListenerContainer.setFailedDeclarationRetryInterval(5000);
    messageListenerContainer.setRetryDeclarationInterval(2);
    return Flux.create(
        emitter -> {
          messageListenerContainer.setMessageListener(
              message -> {
                final var bookingRequestMessage = readBookingRequestMessageValue(message.getBody());
                emitter.next(bookingRequestMessage);
              });

          emitter.onRequest(value -> messageListenerContainer.start());

          emitter.onDispose(messageListenerContainer::stop);
        });
  }

  //  @PostConstruct
  //  public void postConstruct() throws JsonProcessingException {
  //    final var message = new BookingRequestMessage();
  //    message.setCustomerId(UUID.randomUUID());
  //    message.setBedroomId(UUID.randomUUID());
  //    objectMapper.writeValueAsString(message);
  //  }

  @SneakyThrows
  private BookingRequestMessage readBookingRequestMessageValue(byte[] value) {
    return objectMapper.readValue(value, BookingRequestMessage.class);
  }
}
