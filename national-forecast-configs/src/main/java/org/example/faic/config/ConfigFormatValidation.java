package org.example.faic.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.example.faic.config.exception.ConfigFormatException;
import org.example.faic.util.FileReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ConfigFormatValidation {

  public void validateConfig(String configPath, String rootConfigName, ObjectMapper yamlMapper)
      throws ConfigFormatException {
    try {
      // Construct the schema file paths
      String mainSchemaFilePath = "schema/main.schema.json";
      String moduleSchemaFilePath = "schema/module.schema.json";

      // Log the file paths
      log.info("label=VALIDATING_CONFIG, configFile={}, mainSchemaFilePath={}", configPath,
          mainSchemaFilePath);

      // Check if the main configuration file exists
      File configFile = new File(FileReader.getFilePath(configPath, rootConfigName));
      if (!configFile.exists()) {
        throw new ConfigFormatException(
            "Configuration file not found: " + configFile.getAbsolutePath());
      }

      // Log the content of the main configuration file for debugging
      log.debug("label=CONFIG_FILE_CONTENT, content={}", Files.readString(configFile.toPath()));
      String mainConfigSchema = FileReader.readFileFromResources(mainSchemaFilePath);

      // Load the JSON schema for the main configuration
      JsonSchemaFactory schemaFactory =
          JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
      JsonSchema mainSchema = schemaFactory.getSchema(mainConfigSchema);

      // Parse the YAML main configuration file
      JsonNode mainConfigNode = yamlMapper.readTree(configFile);

      // Validate the main configuration against the schema
      Set<ValidationMessage> mainValidationMessages = mainSchema.validate(mainConfigNode);
      if (!mainValidationMessages.isEmpty()) {
        StringBuilder errorMessages =
            new StringBuilder("Main configuration validation failed for file: ")
                .append(configFile.getAbsolutePath()).append("\n");
        for (ValidationMessage message : mainValidationMessages) {
          errorMessages.append(message.getMessage()).append("\n");
        }
        throw new ConfigFormatException(errorMessages.toString());
      }

      // Iterate over all apps
      JsonNode apps = mainConfigNode.get("app");
      if (apps != null && apps.isArray()) {
        String moduleConfigSchema = FileReader.readFileFromResources(moduleSchemaFilePath);
        JsonSchema moduleSchema = schemaFactory.getSchema(moduleConfigSchema);

        for (JsonNode app : apps) {
          JsonNode calls = app.get("call");
          if (calls != null && calls.isArray()) {
            for (JsonNode call : calls) {
              JsonNode moduleConfigs = call.get("moduleConfigs");
              if (moduleConfigs != null && moduleConfigs.isArray()) {
                for (JsonNode moduleConfig : moduleConfigs) {
                  String moduleConfigPath =
                      FileReader.getFilePath(configPath, moduleConfig.get("path").asText());
                  File moduleFile = new File(moduleConfigPath);
                  if (!moduleFile.exists()) {
                    throw new ConfigFormatException(
                        "Module configuration file not found: " + moduleFile.getAbsolutePath());
                  }

                  log.info("label=VALIDATING_MODULE_CONFIG, moduleConfigPath={}", moduleConfigPath);
                  JsonNode moduleConfigNode = yamlMapper.readTree(moduleFile);

                  Set<ValidationMessage> moduleValidationMessages =
                      moduleSchema.validate(moduleConfigNode);
                  if (!moduleValidationMessages.isEmpty()) {
                    StringBuilder errorMessages =
                        new StringBuilder("Module configuration validation failed for file: ")
                            .append(moduleFile.getAbsolutePath()).append("\n");
                    for (ValidationMessage message : moduleValidationMessages) {
                      errorMessages.append(message.getMessage()).append("\n");
                    }
                    throw new ConfigFormatException(errorMessages.toString());
                  }
                }
              }
            }
          }
        }
      }

      log.info("label=CONFIGURATION_SCHEMA_VALIDATION_COMPLETE");
    } catch (IOException e) {
      log.error("label=FAILED_TO_READ_FILE, message={}, stack={}", e.getMessage(),
          e.getStackTrace());
      throw new ConfigFormatException("Error reading configuration file: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new ConfigFormatException("Error validating configuration: " + e.getMessage(), e);
    }
  }
}
