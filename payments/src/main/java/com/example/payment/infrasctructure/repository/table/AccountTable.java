package com.example.payment.infrasctructure.repository.table;

import com.example.payment.domain.model.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Table("ACCOUNTS")
@NoArgsConstructor
public class AccountTable {
  @Id private UUID id;
  private BigDecimal used;

  @Column("MAX_LIMIT")
  private BigDecimal maxLimit;

  public AccountTable(Account account) {
    this.id = account.getCustomer().getId();
    this.used = account.getUsed();
    this.maxLimit = account.getMaxLimit();
  }
}
