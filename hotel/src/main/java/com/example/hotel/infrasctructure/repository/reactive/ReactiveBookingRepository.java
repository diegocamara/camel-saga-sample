package com.example.hotel.infrasctructure.repository.reactive;

import com.example.hotel.infrasctructure.repository.table.BookingTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveBookingRepository extends ReactiveCrudRepository<BookingTable, UUID> {}
