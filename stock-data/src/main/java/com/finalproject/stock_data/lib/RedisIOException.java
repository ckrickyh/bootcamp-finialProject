package com.finalproject.stock_data.lib;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RedisIOException extends JsonProcessingException {
  public RedisIOException(String message) {
    super(message);
  }
}
