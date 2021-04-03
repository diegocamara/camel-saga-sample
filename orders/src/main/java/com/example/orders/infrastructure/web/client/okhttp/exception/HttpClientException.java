package com.example.orders.infrastructure.web.client.okhttp.exception;

import okhttp3.Response;

public class HttpClientException extends RuntimeException {

  private final Response response;

  public HttpClientException(Response response) {
    super(response.message());
    this.response = response;
  }

  public Response getResponse() {
    return response;
  }
}
