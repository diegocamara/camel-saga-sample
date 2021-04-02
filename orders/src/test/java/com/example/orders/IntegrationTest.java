package com.example.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class IntegrationTest {

  protected static final WireMockServer wireMockServer =
      new WireMockServer(
          WireMockConfiguration.options().dynamicPort().notifier(new ConsoleNotifier(true)));

  protected static final MongoDBContainer mongoDBContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:4.4.4"));

  public static final String PAYMENTS_BASE_URL = "PAYMENTS_BASE_URL";
  public static final String DATABASE_PORT = "DATABASE_PORT";

  static {
    wireMockServer.start();
    WireMock.configureFor(wireMockServer.port());
    System.setProperty(PAYMENTS_BASE_URL, wireMockServer.baseUrl());
    mongoDBContainer.start();
    System.setProperty(DATABASE_PORT, mongoDBContainer.getMappedPort(27017).toString());
  }

  protected static final ConfigurableApplicationContext configurableApplicationContext =
      SpringApplication.run(OrdersApplication.class);

  static {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.clearProperty(PAYMENTS_BASE_URL);
                  System.clearProperty(DATABASE_PORT);
                  configurableApplicationContext.stop();
                  wireMockServer.stop();
                }));
  }

  protected static final ObjectMapper objectMapper =
      configurableApplicationContext.getBean(ObjectMapper.class);
  protected static final MongoTemplate mongoTemplate =
      configurableApplicationContext.getBean(MongoTemplate.class);

  @AfterEach
  public void afterEach() {
    wireMockServer.resetAll();
    clearDB();
  }

  private void clearDB() {
    mongoTemplate
        .getCollectionNames()
        .forEach(collection -> mongoTemplate.getCollection(collection).drop());
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
