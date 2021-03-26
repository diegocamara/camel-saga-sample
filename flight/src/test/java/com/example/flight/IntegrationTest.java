package com.example.flight;

import com.example.flight.infrasctructure.repository.reactive.ReactiveOperationRepository;
import com.example.flight.infrasctructure.repository.reactive.ReactiveTicketsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.ConnectionFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;

public class IntegrationTest {

  protected static final ConfigurableApplicationContext configurableApplicationContext =
      SpringApplication.run(FlightApplication.class);
  protected static final ConnectionFactory connectionFactory =
      configurableApplicationContext.getBean(ConnectionFactory.class);
  protected static final ObjectMapper objectMapper =
      configurableApplicationContext.getBean(ObjectMapper.class);
  protected static final ReactiveTicketsRepository reactiveTicketsRepository =
      configurableApplicationContext.getBean(ReactiveTicketsRepository.class);
  protected static final ReactiveOperationRepository reactiveOperationsRepository =
      configurableApplicationContext.getBean(ReactiveOperationRepository.class);
  protected static final R2dbcEntityTemplate r2dbcEntityTemplate =
      configurableApplicationContext.getBean(R2dbcEntityTemplate.class);
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
