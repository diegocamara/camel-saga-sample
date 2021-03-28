package com.example.hotel.infrasctructure.repository.impl;

import com.example.hotel.domain.model.Bedroom;
import com.example.hotel.domain.model.BedroomsRepository;
import com.example.hotel.infrasctructure.repository.reactive.ReactiveBedroomsRepository;
import com.example.hotel.infrasctructure.repository.table.BedroomTable;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCBedroomsRepository implements BedroomsRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;
  private final ReactiveBedroomsRepository reactiveBedroomsRepository;

  @Override
  public Mono<Void> save(Bedroom bedroom) {
    return r2dbcEntityTemplate.insert(new BedroomTable(bedroom)).then();
  }

  @Override
  public Mono<Bedroom> findBedroomById(UUID id) {
    return reactiveBedroomsRepository.findById(id).map(this::bedroom);
  }

  private Bedroom bedroom(BedroomTable bedroomTable) {
    return new Bedroom(bedroomTable.getId(), bedroomTable.getDescription());
  }
}
