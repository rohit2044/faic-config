package org.example.faic.config.exception;

public class ConfigValidationException extends RuntimeException {
  public ConfigValidationException(String message) {
    super(message);
  }
}
