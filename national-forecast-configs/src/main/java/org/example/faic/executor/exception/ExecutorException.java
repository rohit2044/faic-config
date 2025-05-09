package org.example.faic.executor.exception;

public class ExecutorException extends RuntimeException {
  public ExecutorException(String message) {
    super(message);
  }

  public ExecutorException(String callName, String flow, String message) {
    super("INVALID_CALL_NAME: callName=" + callName +
        ", flow=" + flow +
        ", message=" + message);
  }
}
