package com.example.hotel;

import com.example.hotel.application.web.controller.transaction.booking.BookingOperation;
import com.example.hotel.infrasctructure.operation.OperationsRepository;
import com.example.hotel.infrasctructure.repository.impl.R2DBCBedroomsRepository;
import com.example.hotel.infrasctructure.repository.impl.R2DBCBookingRepository;
import com.example.hotel.infrasctructure.repository.impl.R2DBCEntityTemplateOperationsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;

public class IntegrationTest {

  protected static final WireMockServer wireMockServer =
      new WireMockServer(
          WireMockConfiguration.options().dynamicPort().notifier(new ConsoleNotifier(true)));

  public static final String PAYMENTS_BASE_URL = "PAYMENTS_BASE_URL";

  static {
    wireMockServer.start();
    WireMock.configureFor(wireMockServer.port());
    System.setProperty(PAYMENTS_BASE_URL, wireMockServer.baseUrl());
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.clearProperty(PAYMENTS_BASE_URL);
                  wireMockServer.stop();
                }));
  }

  protected static final ConfigurableApplicationContext configurableApplicationContext =
      SpringApplication.run(HotelApplication.class);
  protected static final ConnectionFactory connectionFactory =
      configurableApplicationContext.getBean(ConnectionFactory.class);
  protected static final ObjectMapper objectMapper =
      configurableApplicationContext.getBean(ObjectMapper.class);
  protected static final R2DBCBedroomsRepository r2dbcBedroomsRepository =
      configurableApplicationContext.getBean(R2DBCBedroomsRepository.class);
  protected static final R2DBCBookingRepository r2dbcBookingRepository =
      configurableApplicationContext.getBean(R2DBCBookingRepository.class);
  protected static final OperationsRepository<BookingOperation> r2dbcOperationsRepository =
      configurableApplicationContext.getBean(R2DBCEntityTemplateOperationsRepository.class);
  private static final Resource schemaResource = new ClassPathResource("schema.sql");
  private static final Resource clearDBResource = new ClassPathResource("cleardb.sql");

  @BeforeAll
  public static void beforeAll() {
    createSchema();
  }

  @AfterEach
  public void afterEach() {
    clearDB();
  }

  private static void createSchema() {
    executeScript(schemaResource);
  }

  private void clearDB() {
    executeScript(clearDBResource);
  }

  private static void executeScript(Resource resource) {
    Mono.from(connectionFactory.create())
        .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource))
        .block();
  }

  @SneakyThrows
  protected String writeValueAsString(Object value) {
    return objectMapper.writeValueAsString(value);
  }

  @SneakyThrows
  protected <T> T readValue(String value, Class<T> clazz) {
    return objectMapper.readValue(value, clazz);
  }
}
