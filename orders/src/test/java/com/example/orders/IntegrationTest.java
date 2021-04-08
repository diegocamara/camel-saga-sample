package com.example.orders;

import com.example.orders.infrastructure.repository.springdata.repository.SpringDataOrdersRepository;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

public class IntegrationTest {

  protected static final WireMockServer wireMockServer =
      new WireMockServer(
          WireMockConfiguration.options().dynamicPort().notifier(new ConsoleNotifier(true)));

  protected static final GenericContainer<?> lraCoordinator =
      new GenericContainer<>(
              new ImageFromDockerfile()
                  .withFileFromClasspath("Dockerfile", "lra-coordinator/Dockerfile"))
          .waitingFor(Wait.forLogMessage(".*Installed features.*", 1));

  protected static final MongoDBContainer mongoDBContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:4.4.4"));

  public static final String SERVER_PORT = "SERVER_PORT";
  public static final String LRA_COORDINATOR_BASE_URL = "LRA_COORDINATOR_BASE_URL";
  public static final String LRA_LOCAL_PARTICIPANT_BASE_URL = "LRA_LOCAL_PARTICIPANT_BASE_URL";

  public static final String FLIGHT_SERVICE_BASE_URL = "FLIGHT_SERVICE_BASE_URL";
  public static final String HOTEL_SERVICE_BASE_URL = "HOTEL_SERVICE_BASE_URL";
  public static final String DATABASE_PORT = "DATABASE_PORT";

  static {
    lraCoordinator.start();
    wireMockServer.start();
    WireMock.configureFor(wireMockServer.port());
    System.setProperty(SERVER_PORT, "8080");
    //    System.setProperty(
    //        LRA_COORDINATOR_BASE_URL,
    //        "http://"
    //            + lraCoordinator.getContainerIpAddress()
    //            + ":"
    //            + lraCoordinator.getMappedPort(8080));
    System.setProperty(LRA_COORDINATOR_BASE_URL, "http://localhost:9000");
    System.setProperty(
        LRA_LOCAL_PARTICIPANT_BASE_URL, "http://localhost:" + System.getProperty(SERVER_PORT));

    System.setProperty(FLIGHT_SERVICE_BASE_URL, wireMockServer.baseUrl());
    System.setProperty(HOTEL_SERVICE_BASE_URL, wireMockServer.baseUrl());
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
                  System.clearProperty(SERVER_PORT);
                  System.clearProperty(LRA_COORDINATOR_BASE_URL);
                  System.clearProperty(LRA_LOCAL_PARTICIPANT_BASE_URL);
                  System.clearProperty(FLIGHT_SERVICE_BASE_URL);
                  System.clearProperty(HOTEL_SERVICE_BASE_URL);
                  System.clearProperty(DATABASE_PORT);
                  configurableApplicationContext.stop();
                  wireMockServer.stop();
                  lraCoordinator.stop();
                }));
  }

  protected static final ObjectMapper objectMapper =
      configurableApplicationContext.getBean(ObjectMapper.class);
  protected static final MongoTemplate mongoTemplate =
      configurableApplicationContext.getBean(MongoTemplate.class);
  protected static final SpringDataOrdersRepository springDataOrdersRepository =
      configurableApplicationContext.getBean(SpringDataOrdersRepository.class);

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
