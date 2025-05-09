package org.example.faic.config.exception;

public class ConfigFormatException extends Exception {

  public ConfigFormatException(String message) {
    super(message);
  }

  public ConfigFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
