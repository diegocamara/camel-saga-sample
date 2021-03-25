package com.example.credit.application.web.controller;

import com.example.credit.application.web.model.CreditRequest;
import com.example.credit.application.web.model.CreditResponse;
import com.example.credit.application.web.model.DebitRequest;
import com.example.credit.application.web.model.DebitResponse;
import com.example.credit.domain.feature.AccountCredit;
import com.example.credit.domain.feature.AccountDebit;
import com.example.credit.domain.feature.FindAccountById;
import com.example.credit.domain.model.Client;
import com.example.credit.infrasctructure.repository.OperationRepository;
import com.example.credit.infrasctructure.repository.table.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

  private final FindAccountById findAccountById;
  private final AccountCredit accountCredit;
  private final AccountDebit accountDebit;
  private final OperationRepository operationRepository;

  @PatchMapping("/{clientId}/credit")
  public Mono<ResponseEntity<CreditResponse>> credit(
      @PathVariable("clientId") UUID clientId,
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody CreditRequest creditRequest) {
    return operationRepository
        .create(transactionReference, Operation.CREDIT)
        .then(accountCredit.handle(new Client(clientId), creditRequest.getAmount()))
        .map(account -> ResponseEntity.ok(new CreditResponse(account, transactionReference)))
        .onErrorResume(
            throwable ->
                findAccountById
                    .handle(clientId)
                    .map(
                        account ->
                            ResponseEntity.ok(new CreditResponse(account, transactionReference))));
  }

  @PatchMapping("/{clientId}/debit")
  public Mono<ResponseEntity<DebitResponse>> debit(
      @PathVariable("clientId") UUID clientId,
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody DebitRequest debitRequest) {
    return operationRepository
        .create(transactionReference, Operation.CREDIT)
        .then(accountDebit.handle(new Client(clientId), debitRequest.getAmount()))
        .map(account -> ResponseEntity.ok(new DebitResponse(account, transactionReference)))
        .onErrorResume(
            throwable ->
                findAccountById
                    .handle(clientId)
                    .map(
                        account ->
                            ResponseEntity.ok(new DebitResponse(account, transactionReference))));
  }
}
