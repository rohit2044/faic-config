package org.example.faic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "org.example.faic",
    "io.strati.tunr.utils.client"})
public class NationalForecastServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(NationalForecastServiceApplication.class, args);
  }
}
