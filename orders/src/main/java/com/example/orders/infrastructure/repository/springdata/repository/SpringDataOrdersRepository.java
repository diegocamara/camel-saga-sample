package com.example.orders.infrastructure.repository.springdata.repository;

import com.example.orders.infrastructure.repository.springdata.document.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface SpringDataOrdersRepository extends MongoRepository<OrderDocument, UUID> {}
