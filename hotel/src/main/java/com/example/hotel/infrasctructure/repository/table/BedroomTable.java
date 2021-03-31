package com.example.hotel.infrasctructure.repository.table;

import com.example.hotel.domain.model.Bedroom;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("bedrooms")
public class BedroomTable {
  @Id private UUID id;
  private String description;
  private BigDecimal price;

  public BedroomTable(Bedroom bedroom) {
    this.id = bedroom.getId();
    this.description = bedroom.getDescription();
    this.price = bedroom.getPrice();
  }
}
