package com.example.hotel.infrasctructure.repository.reactive;

import com.example.hotel.infrasctructure.repository.table.BedroomTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveBedroomsRepository extends ReactiveCrudRepository<BedroomTable, UUID> {}
