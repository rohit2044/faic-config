package org.example.faic.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.faic.config.exception.ConfigFormatException;
import org.example.faic.config.exception.ConfigScooperException;
import org.example.faic.model.ConsumerConfig;
import org.example.faic.model.ConsumerConfig.Consumers;
import org.example.faic.model.executor.ProcedureTypes;
import org.example.faic.model.module.ExecutableModules;
import org.example.faic.model.module.ModuleConfig;
import org.example.faic.util.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ConfigScooper {

  private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  // Map of consumer name to its registered calls
  private final Map<String, Set<String>> consumerKeys = new HashMap<>();
  /**
   * Key: String - The name of the call (e.g., "LINE_REVIEW_NATIONAL_FORECAST").
   * Value: Map of String, ExecutableModules - A nested map where:
   *   - Key: String - The name of the module (e.g., "REQUEST_PROCESSING").
   *   - Value: ExecutableModules - The type of the module (e.g., IMDB, PACT, CSV_GCS, etc.).
   */
  private final Map<String, Map<String, ExecutableModules>> callModuleType = new HashMap<>();

  // Map of call to config
  private final Map<String, ConsumerConfig.ConsumerCall> callConfigMap = new HashMap<>();

  // Map of consumer call name to its module config
  private final Map<String,
      Map<String, ModuleConfig<ProcedureTypes>>> callModuleMap = new HashMap<>();

  protected void loadConfig(String configPath, String rootConfigName) throws ConfigFormatException {
    try {
      ConfigFormatValidation.validateConfig(configPath, rootConfigName, mapper);
      var rootConfigPath = FileReader.getFilePath(configPath, rootConfigName);
      log.info("label=LOADING_ROOT_CONFIG, rootConfigPath={}", rootConfigPath);
      var consumers = mapper.readValue(
          new File(rootConfigPath), Consumers.class);

      for (var consumer : consumers.consumers()) {
        this.consumerKeys.putIfAbsent(consumer.name(), new HashSet<>());

        for (var consumerCall : consumer.call()) {
          this.consumerKeys.get(consumer.name()).add(consumerCall.name());
          callConfigMap.put(consumerCall.name(), consumerCall);
          loadModuleConfig(configPath, consumerCall);
        }
      }
      log.info("label=CONSUMER_CONFIGS_LOAD_COMPLETE, " +
          "consumers={}, consumerCalls={}", consumerKeys.keySet(),
          callConfigMap.values().stream().map(ConsumerConfig.ConsumerCall::name).toList());
    } catch (IOException e) {
      log.error("label=FAILED_TO_LOAD_MAIN_CONFIG, message={}, stack={}",
          e.getMessage(), e.getStackTrace());
      throw new ConfigScooperException(e.getMessage());
    }
  }

  private void loadModuleConfig(
      String configPath,
      ConsumerConfig.ConsumerCall consumerCall
  ) throws IOException {
    for (var moduleConfig : consumerCall.moduleConfigs()) {
      var moduleConfigType = moduleConfig.type();
      var moduleName = moduleConfig.name();
      callModuleType.putIfAbsent(consumerCall.name(), new HashMap<>());
      callModuleType.get(consumerCall.name()).put(moduleName, moduleConfigType);

      var moduleConfigPath = configPath + moduleConfig.path();
      log.info("label=LOADING_MODULE_CONFIG, " +
          "moduleConfigPath={}, moduleName={}, " +
          "moduleConfigType={}", moduleConfigPath, moduleName, moduleConfigType);
      var file = new File(moduleConfigPath);
      var config = mapper.readValue(file,
          new TypeReference<ModuleConfig<ProcedureTypes>>() {});
      callModuleMap.putIfAbsent(consumerCall.name(), new HashMap<>());
      callModuleMap.get(consumerCall.name())
          .put(moduleName, config);
    }
  }
}
