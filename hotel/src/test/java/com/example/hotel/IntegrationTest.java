package com.example.hotel;

import com.example.hotel.infrasctructure.repository.impl.R2DBCBedroomsRepository;
import com.example.hotel.infrasctructure.repository.impl.R2DBCBookingRepository;
import com.example.hotel.infrasctructure.repository.impl.R2DBCOperationsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  protected static final R2DBCOperationsRepository r2dbcOperationsRepository =
      configurableApplicationContext.getBean(R2DBCOperationsRepository.class);
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
