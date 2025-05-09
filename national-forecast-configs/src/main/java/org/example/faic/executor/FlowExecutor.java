package org.example.faic.executor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.faic.config.ConfigGovernor;
import org.example.faic.executor.exception.ExecutorException;
import org.example.faic.model.executor.ExecutorProcedureConfig;
import org.example.faic.model.executor.FlowTypes;
import org.example.faic.model.executor.ProcedureTypes;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class FlowExecutor {
  private final ConfigGovernor configGovernor;

  public FlowExecutor(ConfigGovernor configGovernor) {
    this.configGovernor = configGovernor;
  }

  public ObjectNode executeFlow(String callName, FlowTypes flowType, ObjectNode params) {
    if (!configGovernor.isValidCall(callName)) {
      throw new ExecutorException(callName, flowType.name(), "call not registered");
    }

    List<ExecutorProcedureConfig<ProcedureTypes>> procedures =
        configGovernor.getFlowProcedure(flowType, callName);
    ObjectNode resultNode = configGovernor.getMapper().createObjectNode();
    ObjectNode currentParams = params;

    for (ExecutorProcedureConfig<ProcedureTypes> procedure : procedures) {
      String[] sourceParts = procedure.source().split("\\.");
      String moduleName = sourceParts[0];
      String executorName = sourceParts[1];

      log.debug("Executing MODULE_EXECUTOR: module={}, executor={}", moduleName, executorName);
      currentParams = executeModule(callName, moduleName, executorName, currentParams);

      resultNode.set(procedure.resultKey(), currentParams);
    }

    return resultNode;
  }

  private ObjectNode executeModule(String callName, String moduleName, String executorName,
                                   ObjectNode params) {
    var moduleConfig = configGovernor.getCallModuleMap()
        .get(callName).get(moduleName);

    var executor = moduleConfig.executor().stream()
        .filter(e -> e.name().equals(executorName))
        .findFirst()
        .orElseThrow(() -> new ExecutorException("Executor not found: " + executorName));

    ObjectNode result = configGovernor.getMapper().createObjectNode();
    for (var procedure : executor.procedure()) {
      if (procedure.type() == ProcedureTypes.MODULE_EXECUTOR) {
        String[] sourceParts = procedure.source().split("\\.");
        String subModuleName = sourceParts[0];
        String subExecutorName = sourceParts[1];

        result = executeModule(callName, subModuleName, subExecutorName, params);
      } else if (procedure.type() == ProcedureTypes.MODULE_MAPPING) {
        String[] sourceParts = procedure.source().split("\\.");
        String subModuleName = sourceParts[0];
        String subMappingName = sourceParts[1];

        result = executeMapping(callName, subModuleName, subMappingName, params);
      } else {
        try {
          MDC.put("callName", callName);
          MDC.put("moduleName", moduleName);
          MDC.put("executorName", executorName);

          result = executeExecutorType(procedure.type(), procedure.source(), params);
        } catch (Exception e) {
          log.error("label=EXECUTOR_TYPE_ERROR, message={}", e.getMessage());
          throw new ExecutorException("Error executing executor type: " + procedure.type());
        } finally {
          MDC.clear();
        }
      }
    }

    return result;
  }

  private ObjectNode executeMapping(String callName, String moduleName, String mappingName,
                                    ObjectNode params) {
    var moduleConfig = configGovernor.getCallModuleMap()
        .get(callName).get(moduleName);

    var mapping = moduleConfig.mapping().stream()
        .filter(m -> m.name().equals(mappingName))
        .findFirst()
        .orElseThrow(() -> new ExecutorException("Mapping not found: " + mappingName));

    ObjectNode result = configGovernor.getMapper().createObjectNode();
    mapping.source().forEach(source -> {
      String value = params.path(source.mapping()).asText(source.defaultValue());
      result.put(source.name(), value);
    });

    return result;
  }

  private ObjectNode executeExecutorType(ProcedureTypes type, String source, ObjectNode params) {
    log.info("label=EXECUTING_EXECUTOR_TYPE, type={}", type);
    log.debug("label=EXECUTING_EXECUTOR_TYPE, type={}, source={}", type, source);
    ObjectNode result = configGovernor.getMapper().createObjectNode();
    result.put("result", "Executed " + type + " with source: " + source);
    return result;
  }
}
