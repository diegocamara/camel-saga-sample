package com.example.credit;

import com.example.credit.application.web.controller.AccountController;
import com.example.credit.application.web.model.DebitRequest;
import com.example.credit.infrasctructure.repository.OperationRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@AllArgsConstructor
@SpringBootApplication
public class CreditApplication implements ApplicationRunner {

  private final OperationRepository operationRepository;
  private final AccountController accountController;

  public static void main(String[] args) {
    SpringApplication.run(CreditApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    final var clientId = UUID.fromString("2aa2ae05-3761-4d4a-926b-6a27031192e5");
    final var operationId = UUID.fromString("adb40d67-62a4-4432-9de5-d5a1b04cd151");

    //    operationRepository.findById(operationId).subscribe();
    accountController.debit(clientId, operationId, new DebitRequest()).subscribe();
  }
}
