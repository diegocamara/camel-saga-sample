package com.example.flight.infrasctructure.gateway;

import com.example.flight.domain.model.Account;
import com.example.flight.domain.model.AccountDebitInput;
import com.example.flight.domain.model.PaymentsGateway;
import com.example.flight.infrasctructure.gateway.model.DebitRequest;
import com.example.flight.infrasctructure.gateway.model.DebitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SpringReactiveWebClientPaymentsGateway implements PaymentsGateway {

  @Autowired
  @Qualifier("paymentsWebClient")
  private WebClient webClient;

  @Override
  public Mono<Account> debit(AccountDebitInput accountDebitInput) {
    return webClient
        .patch()
        .uri("/accounts/" + accountDebitInput.getAccountId().toString() + "/debit")
        .header("transaction-reference", UUID.randomUUID().toString())
        .body(Mono.just(new DebitRequest(accountDebitInput)), DebitRequest.class)
        .exchangeToMono(
            clientResponse ->
                clientResponse
                    .bodyToMono(DebitResponse.class)
                    .map(debitResponse -> new Account(debitResponse.getCustomer())));
  }
}
