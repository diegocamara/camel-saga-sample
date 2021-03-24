package com.example.credit.application.web.controller;

import com.example.credit.application.web.model.ConsumeRequest;
import com.example.credit.application.web.model.ConsumeResponse;
import com.example.credit.domain.feature.AccountDebit;
import com.example.credit.domain.model.Client;
import com.example.credit.infrasctructure.repository.OperationRepository;
import com.example.credit.infrasctructure.repository.table.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {

  private final OperationRepository operationRepository;
  private final AccountDebit accountDebit;

  @Transactional
  @PatchMapping("/{clientId}/consume")
  public Mono<ResponseEntity<ConsumeResponse>> debit(
      @PathVariable("clientId") UUID clientId,
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody ConsumeRequest consumeRequest) {
    return operationRepository
        .create(transactionReference, Operation.CONSUME)
        .then(accountDebit.handle(new Client(clientId), consumeRequest.getAmount()))
        .map(credit -> ResponseEntity.ok(new ConsumeResponse(credit, transactionReference)))
        .onErrorResume(throwable -> Mono.just(ResponseEntity.ok().build()));
  }
}
