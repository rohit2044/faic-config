package org.example.faic.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.faic.config.ConfigGovernor;
import org.example.faic.executor.FlowExecutor;
import org.example.faic.executor.exception.ExecutorException;
import org.example.faic.model.Faic;
import org.example.faic.model.executor.FlowTypes;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/v1")
public class FaicController {
  private final FlowExecutor flowExecutor;

  @Autowired
  public FaicController(ConfigGovernor configGovernor) {
    this.flowExecutor = new FlowExecutor(configGovernor);
  }

  @PostMapping(
      value = "/forecast",
      consumes = {"application/json"})
  public ResponseEntity<JsonNode> submitForecastRequest(
      @Valid @RequestBody Faic.Request request) {

    try {
      return ResponseEntity.ok(
          this.flowExecutor.executeFlow(request.requestType(), FlowTypes.REQUEST, request.request()));
    } catch (ExecutorException e) {
      log.error("label=REQUEST_FAILED, message={}, stack={}",
          e.getMessage(), e.getStackTrace());
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(
      value = "/forecast",
      consumes = {"application/octet-stream", "multipart/form-data"})
  public ResponseEntity<JsonNode> submitForecastRequest(
      @RequestPart("request") String request,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    return null;
  }
}
