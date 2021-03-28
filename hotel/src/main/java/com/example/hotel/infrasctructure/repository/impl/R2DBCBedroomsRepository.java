package com.example.hotel.infrasctructure.repository.impl;

import com.example.hotel.domain.model.Bedroom;
import com.example.hotel.domain.model.BedroomsRepository;
import com.example.hotel.infrasctructure.repository.reactive.ReactiveBedroomsRepository;
import com.example.hotel.infrasctructure.repository.table.BedroomTable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCBedroomsRepository implements BedroomsRepository {

  private final ReactiveBedroomsRepository reactiveBedroomsRepository;

  @Override
  public Mono<Bedroom> findBedroomById(UUID id) {
    return reactiveBedroomsRepository.findById(id).map(this::bedroom);
  }

  private Bedroom bedroom(BedroomTable bedroomTable) {
    return new Bedroom(bedroomTable.getId(), bedroomTable.getDescription());
  }
}
