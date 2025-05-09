package org.example.faic.config;

import org.example.faic.config.exception.ConfigValidationException;
import org.example.faic.model.executor.ExecutorProcedureConfig;
import org.example.faic.model.executor.ProcedureTypes;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ConfigValidation {

  public void validateConfigs(ConfigGovernor configGovernor) {
    validateFlows(configGovernor);
    validateModules(configGovernor);
    log.info("label=CONFIG_VALIDATION_COMPLETE");
  }

  private void validateFlows(ConfigGovernor configGovernor) {
    for (var entry : configGovernor.getCallConfigMap().entrySet()) {
      var callName = entry.getKey();
      var consumerCall = entry.getValue();

      validateProcedures(configGovernor, callName, consumerCall.initFlow());
      validateProcedures(configGovernor, callName, consumerCall.requestFlow());
    }
  }

  private void validateModules(ConfigGovernor configGovernor) {
    for (var entry : configGovernor.getCallModuleMap().entrySet()) {
      var callName = entry.getKey();
      var moduleConfigs = entry.getValue();

      for (var moduleEntry : moduleConfigs.entrySet()) {
        var moduleConfig = moduleEntry.getValue();
        if (Objects.isNull(moduleConfig.executor())) {
          continue;
        }

        validateProcedures(configGovernor, callName, moduleConfig.executor().stream()
            .flatMap(executorConfig -> executorConfig.procedure().stream())
            .toList());
      }
    }
  }

  private void validateProcedures(
      ConfigGovernor configGovernor,
      String callName,
      List<ExecutorProcedureConfig<ProcedureTypes>> procedures) {
    if (procedures == null) {
      return;
    }

    for (var procedure : procedures) {
      var type = procedure.type();
      if (type == ProcedureTypes.MODULE_EXECUTOR || type == ProcedureTypes.MODULE_MAPPING) {
        validateProcedureSource(configGovernor, callName, procedure.source());
      }
    }
  }

  private void validateProcedureSource(
      ConfigGovernor configGovernor, String callName, String source) {
    if (source == null || !source.contains(".")) {
      throw new ConfigValidationException(
          "Invalid source format in call: " + callName + ", source: " + source);
    }

    var parts = source.split("\\.");
    if (parts.length != 2) {
      throw new ConfigValidationException(
          "Invalid source format in call: " + callName + ", source: " + source);
    }

    var moduleName = parts[0];
    var executorName = parts[1];

    var moduleMap = configGovernor.getCallModuleMap().get(callName);
    if (moduleMap == null || !moduleMap.containsKey(moduleName)) {
      throw new ConfigValidationException(
          "Module not found: " + moduleName + " in call: " + callName);
    }

    var moduleConfig = moduleMap.get(moduleName);
    var executor = moduleConfig.executor().stream().filter(
            executorConfig -> executorConfig.name().equals(executorName))
        .findFirst();
    if (executor.isEmpty()) {
      throw new ConfigValidationException(
          "Executor not found: " + executorName + " in module: " + moduleName + " for call: " +
              callName);
    }
    if (executor.get().procedure().isEmpty()) {
      throw new ConfigValidationException(
          "No procedure found for executor : " + executorName + " in module: " + moduleName +
              " for call: " +
              callName);
    }
  }
}
