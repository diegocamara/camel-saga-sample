package com.example.hotel.infrasctructure.gateway;

import com.example.hotel.domain.model.*;
import com.example.hotel.infrasctructure.gateway.model.CreditRequest;
import com.example.hotel.infrasctructure.gateway.model.CreditResponse;
import com.example.hotel.infrasctructure.gateway.model.DebitRequest;
import com.example.hotel.infrasctructure.gateway.model.DebitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SpringReactiveWebClientPaymentsGateway implements PaymentsGateway {

  public static final String TRANSACTION_REFERENCE_HEADER = "transaction-reference";

  @Autowired
  @Qualifier("paymentsWebClient")
  private WebClient webClient;

  @Override
  public Mono<Account> debit(AccountDebitInput accountDebitInput) {
    return webClient
        .patch()
        .uri("/accounts/" + accountDebitInput.getAccountId().toString() + "/debit")
        .header(TRANSACTION_REFERENCE_HEADER, UUID.randomUUID().toString())
        .body(Mono.just(new DebitRequest(accountDebitInput)), DebitRequest.class)
        .exchangeToMono(
            clientResponse ->
                clientResponse
                    .bodyToMono(DebitResponse.class)
                    .map(
                        debitResponse ->
                            new Account(
                                debitResponse.getCustomer(),
                                new Details(debitResponse.getUsed()))));
  }

  @Override
  public Mono<Account> credit(AccountCreditInput accountCreditInput) {
    return webClient
        .patch()
        .uri("/accounts/" + accountCreditInput.getAccountId().toString() + "/credit")
        .header(TRANSACTION_REFERENCE_HEADER, UUID.randomUUID().toString())
        .body(Mono.just(new CreditRequest(accountCreditInput.getAmount())), CreditRequest.class)
        .exchangeToMono(
            clientResponse ->
                clientResponse
                    .bodyToMono(CreditResponse.class)
                    .map(
                        creditResponse ->
                            new Account(
                                creditResponse.getCustomer(),
                                new Details(creditResponse.getUsed()))));
  }
}
