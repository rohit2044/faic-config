package org.example.faic.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface Faic {
  record Request(
      @NotBlank(message = "requestType cannot be empty") String requestType,
      @NotBlank(message = "createdBy cannot be empty") String createdBy,
      @NotNull(message = "request cannot be empty") ObjectNode request) {}

}
