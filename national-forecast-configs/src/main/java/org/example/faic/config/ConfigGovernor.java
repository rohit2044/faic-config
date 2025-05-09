package org.example.faic.config;

import org.example.faic.config.exception.ConfigFormatException;
import org.example.faic.config.exception.ConfigGovernorException;
import org.example.faic.model.ConsumerConfig;
import org.example.faic.model.executor.ExecutorProcedureConfig;
import org.example.faic.model.executor.FlowTypes;
import org.example.faic.model.executor.ProcedureTypes;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigGovernor extends ConfigScooper {

  public ConfigGovernor(String configPath, String rootConfigName) throws ConfigFormatException {
    super.loadConfig(configPath, rootConfigName);
    ConfigValidation.validateConfigs(this);
    log.info("label=CONFIG_LOADED, configPath={}, rootConfig={}", configPath, rootConfigName);
  }

  public Set<String> getRegisteredConsumers() {
    return super.getConsumerKeys().keySet();
  }

  public Set<String> getRegisteredConsumerCalls(String consumer) {
    return super.getConsumerKeys().get(consumer);
  }

  public List<ExecutorProcedureConfig<ProcedureTypes>> getFlowProcedure(
      FlowTypes flowType, String callName) {
    switch (flowType) {
      case INIT -> {
        return super.getCallConfigMap().get(callName).initFlow();
      }
      case REQUEST -> {
        return super.getCallConfigMap().get(callName).requestFlow();
      }
      default -> throw new ConfigGovernorException(callName);
    }
  }

  public boolean isValidCall(String callName) {
    return super.getCallConfigMap().containsKey(callName);
  }

  public List<ExecutorProcedureConfig<ProcedureTypes>> getCallExecutionFlow(
      String callName) {
    checkIfCallExists(callName);
    return super.getCallConfigMap().get(callName).requestFlow();
  }

  public List<String> getRegisteredCallModules(String callName) {
    checkIfCallExists(callName);
    return super.getCallConfigMap().get(callName)
        .moduleConfigs().stream().map(
            ConsumerConfig.ConfigMetadata::name).toList();
  }

  private void checkIfCallExists(String callName) {
    if (Objects.isNull(super.getCallConfigMap().get(callName))) {
      throw new ConfigGovernorException(callName);
    }
  }
}
