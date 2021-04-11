package com.example.flight.infrastructure.operation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class OperationNotRegisteredException extends RuntimeException {}
