package org.example.faic.config.exception;

public class ConfigGovernorException extends RuntimeException {
  public ConfigGovernorException(String callName) {
    super("INVALID_CALL_NAME_REQUESTED " + callName);
  }

  public ConfigGovernorException(String callName, String moduleName) {
    super("INVALID_CALL_MODULE_REQUESTED callName=" + callName + " moduleName=" + moduleName);
  }

  public ConfigGovernorException(String callName, String moduleName, String executorName) {
    super("INVALID_CALL_MODULE_REQUESTED callName=" +
        callName + " moduleName=" + moduleName +
        " executorName=" + executorName);
  }
}
