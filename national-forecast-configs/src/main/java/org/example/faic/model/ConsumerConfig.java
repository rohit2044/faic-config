package org.example.faic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.faic.model.executor.ExecutorProcedureConfig;
import org.example.faic.model.executor.ProcedureTypes;
import org.example.faic.model.module.ExecutableModules;
import java.util.List;

public interface ConsumerConfig {
  record Consumers(@JsonProperty("app") List<Consumer> consumers) {}

  record Consumer(String name, List<ConsumerCall> call) {}

  record ConsumerCall(
      String name,
      List<ExecutorProcedureConfig<ProcedureTypes>> initFlow,
      List<ExecutorProcedureConfig<ProcedureTypes>> requestFlow,
      List<ConfigMetadata> moduleConfigs) {}

  record ConfigMetadata(String name, ExecutableModules type, String path) {}
}
