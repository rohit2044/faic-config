package org.example.faic.config.ccm;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "configManager")
public interface FaicConfigManager {
  @Property(propertyName = "gcpProjectId")
  String gcsProjectId();

  @Property(propertyName = "gcsBucketName")
  String gcsBucketName();

  @Property(propertyName = "gcsConfigPath")
  String gcsConfigPath();

  @Property(propertyName = "importPath")
  String importPath();

  @Property(propertyName = "rootConfigName")
  String rootConfigName();
}
