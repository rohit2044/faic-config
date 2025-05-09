package org.example.faic.model.executor;

public enum ProcedureTypes {
  /**
   * Used for getting executor/mapping config to execute
   * from other modules.
   */
  MODULE_MAPPING, MODULE_EXECUTOR,

  /**
   * Used for CsvGcs.
   */
  CONSTRUCT_CSV,

  /**
   * Used for SQL Query.
   */
  CREATE_QUERY, SELECT_QUERY, INSERT_QUERY, UPDATE_QUERY, DELETE_QUERY,

  /**
   * Used for Pact.
   */
  JSON_SCHEMA_VALIDATION,

  /**
   * Used for Mapping.
   */
  EXPORT_MAPPING, PARAM_MAPPING
}
