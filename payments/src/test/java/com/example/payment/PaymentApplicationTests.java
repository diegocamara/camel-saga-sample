package com.example.payment;

import com.example.payment.application.web.model.CreditRequest;
import com.example.payment.application.web.model.DebitRequest;
import com.example.payment.infrasctructure.repository.table.AccountTable;
import com.example.payment.infrasctructure.repository.table.Operation;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

class PaymentApplicationTests extends IntegrationTest {

  public static final String TRANSACTION_REFERENCE_HEADER = "transaction-reference";
  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldDebitTwiceFromCustomerAccount() {

    final var account = new AccountTable();
    account.setId(customerId);
    account.setMaxLimit(BigDecimal.valueOf(100));
    account.setUsed(BigDecimal.ZERO);

    r2dbcEntityTemplate.insert(account).block();

    final var debitRequest = new DebitRequest();
    debitRequest.setAmount(BigDecimal.valueOf(10));

    final var firstDebitRequestTransactionReference = UUID.randomUUID().toString();

    var firstDebitRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(TRANSACTION_REFERENCE_HEADER, firstDebitRequestTransactionReference)
            .body(writeValueAsString(debitRequest));

    final var url = "/accounts/" + customerId.toString() + "/debit";

    final var firstDebitResponse = firstDebitRequestSpecification.patch(url);

    firstDebitResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(10),
            "transactionId",
            CoreMatchers.is(firstDebitRequestTransactionReference));

    final var secondDebitRequestTransactionReference = UUID.randomUUID().toString();

    var secondDebitRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(TRANSACTION_REFERENCE_HEADER, secondDebitRequestTransactionReference)
            .body(writeValueAsString(debitRequest));

    final var secondDebitResponse = secondDebitRequestSpecification.patch(url);

    secondDebitResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(20),
            "transactionId",
            CoreMatchers.is(secondDebitRequestTransactionReference));
  }

  @Test
  void testDebitIdempotency() {

    final var account = new AccountTable();
    account.setId(customerId);
    account.setMaxLimit(BigDecimal.valueOf(100));
    account.setUsed(BigDecimal.ZERO);

    r2dbcEntityTemplate.insert(account).block();

    final var debitRequest = new DebitRequest();
    debitRequest.setAmount(BigDecimal.valueOf(10));

    final var transactionReference = UUID.randomUUID().toString();

    final var debitRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(TRANSACTION_REFERENCE_HEADER, transactionReference)
            .body(writeValueAsString(debitRequest));

    final var url = "/accounts/" + customerId.toString() + "/debit";

    final var firstDebitResponse = debitRequestSpecification.patch(url);

    firstDebitResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(10),
            "transactionId",
            CoreMatchers.is(transactionReference));

    final var secondDebitResponse = debitRequestSpecification.patch(url);

    secondDebitResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(10),
            "transactionId",
            CoreMatchers.is(transactionReference));

    final var storedAccount = reactiveAccountRepository.findById(customerId).block();

    Assertions.assertNotNull(storedAccount);
    Assertions.assertEquals(BigDecimal.TEN, storedAccount.getUsed());

    final var storedOperation =
        reactiveOperationsRepository.findById(UUID.fromString(transactionReference)).block();

    Assertions.assertNotNull(storedOperation);
    Assertions.assertEquals(Operation.DEBIT, storedOperation.getOperation());
  }

  @Test
  void shouldCreditTwiceFromCustomerAccount() {

    final var account = new AccountTable();
    account.setId(customerId);
    account.setMaxLimit(BigDecimal.valueOf(100));
    account.setUsed(BigDecimal.valueOf(50));

    r2dbcEntityTemplate.insert(account).block();

    final var creditRequest = new CreditRequest();
    creditRequest.setAmount(BigDecimal.valueOf(10));

    final var firstCreditRequestTransactionReference = UUID.randomUUID().toString();

    var firstCreditRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(TRANSACTION_REFERENCE_HEADER, firstCreditRequestTransactionReference)
            .body(writeValueAsString(creditRequest));

    final var url = "/accounts/" + customerId.toString() + "/credit";

    final var firstCreditResponse = firstCreditRequestSpecification.patch(url);

    firstCreditResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(40),
            "transactionId",
            CoreMatchers.is(firstCreditRequestTransactionReference));

    final var secondCreditRequestTransactionReference = UUID.randomUUID().toString();

    var secondCreditRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(TRANSACTION_REFERENCE_HEADER, secondCreditRequestTransactionReference)
            .body(writeValueAsString(creditRequest));

    final var secondCreditResponse = secondCreditRequestSpecification.patch(url);

    secondCreditResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(30),
            "transactionId",
            CoreMatchers.is(secondCreditRequestTransactionReference));
  }

  @Test
  void testCreditIdempotency() {

    final var account = new AccountTable();
    account.setId(customerId);
    account.setMaxLimit(BigDecimal.valueOf(100));
    account.setUsed(BigDecimal.valueOf(50));

    r2dbcEntityTemplate.insert(account).block();

    final var creditRequest = new CreditRequest();
    creditRequest.setAmount(BigDecimal.valueOf(10));

    final var transactionReference = UUID.randomUUID().toString();

    final var creditRequestSpecification =
        RestAssured.given()
            .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(TRANSACTION_REFERENCE_HEADER, transactionReference)
            .body(writeValueAsString(creditRequest));

    final var url = "/accounts/" + customerId.toString() + "/credit";

    final var firstCreditResponse = creditRequestSpecification.patch(url);

    firstCreditResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(40),
            "transactionId",
            CoreMatchers.is(transactionReference));

    final var secondCreditResponse = creditRequestSpecification.patch(url);

    secondCreditResponse
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "customer",
            CoreMatchers.is(customerId.toString()),
            "used",
            CoreMatchers.is(40),
            "transactionId",
            CoreMatchers.is(transactionReference));

    final var storedAccount = reactiveAccountRepository.findById(customerId).block();

    Assertions.assertNotNull(storedAccount);
    Assertions.assertEquals(BigDecimal.valueOf(40), storedAccount.getUsed());

    final var storedOperation =
        reactiveOperationsRepository.findById(UUID.fromString(transactionReference)).block();

    Assertions.assertNotNull(storedOperation);
    Assertions.assertEquals(Operation.CREDIT, storedOperation.getOperation());
  }
}
