package com.example.hotel.domain.model;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BedroomsRepository {

  Mono<Void> save(Bedroom bedroom);

  Mono<Bedroom> findBedroomById(UUID id);
}
