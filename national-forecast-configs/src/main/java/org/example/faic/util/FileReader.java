package org.example.faic.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileReader {

  public String readFileFromResources(String fileName) throws Exception {
    // Get the resource as an InputStream
    ClassLoader classLoader = FileReader.class.getClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
      if (inputStream == null) {
        throw new IllegalArgumentException("File not found in resources: " + fileName);
      }

      // Convert InputStream to String
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }

  public String getFilePath(String directoryPath, String fileName) {
    return String.join("/", directoryPath, fileName);
  }
}
