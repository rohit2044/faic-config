package org.example.faic.model.mapping;

import org.example.faic.model.executor.ProcedureTypes;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface MappingConfig {

  record Mapping(String name, ProcedureTypes type, List<MappingSource> source) {}

  record MappingSource(
      String name, String mapping,
      String defaultValue, MappingType type,
      Set<String> values, String source) {
    public MappingSource(
        String name, String mapping,
        String defaultValue, MappingType type,
        Set<String> values, String source) {
      this.name = name;
      this.mapping = mapping;
      this.defaultValue = defaultValue;
      this.type = Objects.isNull(type) ? MappingType.STRING : type;
      this.values = values;
      this.source = source;
    }
  }

}
