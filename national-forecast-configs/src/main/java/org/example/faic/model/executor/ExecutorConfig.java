package org.example.faic.model.executor;

import java.util.List;

public record ExecutorConfig<E>(
    String name, List<ExecutorProcedureConfig<E>> procedure) {}
