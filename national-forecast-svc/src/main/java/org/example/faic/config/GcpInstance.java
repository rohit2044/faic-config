package org.example.faic.config;

import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.transfermanager.TransferManagerConfig;
import org.example.faic.config.ccm.FaicConfigManager;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcpInstance {

  @ManagedConfiguration
  private FaicConfigManager faicConfigManager;

  @Bean
  public StorageOptions storage() {
    return StorageOptions.grpc()
        .setProjectId(faicConfigManager.gcsProjectId())
        .setAttemptDirectPath(false)
        .build();
  }

  @Bean
  public TransferManagerConfig transferManager() {
    return TransferManagerConfig.newBuilder()
        .setMaxWorkers(3)
        .setStorageOptions(storage())
        .build();
  }

}
