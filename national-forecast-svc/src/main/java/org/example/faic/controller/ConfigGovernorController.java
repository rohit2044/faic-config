package org.example.faic.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.faic.config.ConfigGovernor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigGovernorController {

  private final ConfigGovernor configManager;

  @Autowired
  public ConfigGovernorController(ConfigGovernor configManager) {
    this.configManager = configManager;
  }

  @GetMapping("/v1/config")
  public Object getConfig(@RequestBody JsonNode jsonNode) {
    try {
      var methodName = jsonNode.get("methodName");

      if (methodName.isNull()) {
        return ResponseEntity.badRequest().build();
      }

      // Collect parameters dynamically
      Object[] params = collectParams(jsonNode);
      Class<?>[] paramTypes = getParamTypes(params);

      // Retrieve and invoke the method
      var method = configManager.getClass().getMethod(methodName.asText(), paramTypes);
      return method.invoke(configManager, params);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  private Object[] collectParams(JsonNode jsonNode) {
    var params = List.of("consumerOrCallName", "moduleName", "executorName");

    return params.stream()
        .map(jsonNode::get)
        .filter(jnode -> Objects.nonNull(jnode) && !jnode.isNull())
        .map(JsonNode::asText)
        .toArray();
  }

  private Class<?>[] getParamTypes(Object[] params) {
    return Arrays.stream(params)
        .map(Object::getClass)
        .toArray(Class<?>[]::new);
  }
}
