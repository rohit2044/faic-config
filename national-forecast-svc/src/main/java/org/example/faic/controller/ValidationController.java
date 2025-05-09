package org.example.faic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {
  @GetMapping("/v1/requests")
  public String validate() {
    return "Validation successful!";
  }
}
