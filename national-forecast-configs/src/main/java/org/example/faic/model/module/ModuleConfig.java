package org.example.faic.model.module;

import org.example.faic.model.executor.ExecutorConfig;
import org.example.faic.model.mapping.MappingConfig;
import java.util.List;

public record ModuleConfig<E>(
    List<MappingConfig.Mapping> mapping, List<ExecutorConfig<E>> executor) {}
