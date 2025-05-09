package org.example.faic.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.transfermanager.TransferManager;
import com.google.cloud.storage.transfermanager.TransferManagerConfig;
import org.example.faic.config.ccm.FaicConfigManager;
import org.example.faic.config.exception.ConfigFormatException;
import org.example.faic.util.GcsUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfigInstance {

  @ManagedConfiguration
  private FaicConfigManager faicConfigManager;
  private final Storage storage;
  private final TransferManager transferManager;

  @Autowired
  public ConsumerConfigInstance(
      StorageOptions storage, TransferManagerConfig transferManagerConfig) {
    this.storage = storage.getService();
    this.transferManager = transferManagerConfig.getService();
  }

  @Bean
  public ConfigGovernor configGovernor() throws ConfigFormatException {
    GcsUtil.downloadFolder(
        storage, transferManager,
        faicConfigManager.gcsBucketName(),
        faicConfigManager.gcsConfigPath(),
        faicConfigManager.importPath());

    return new ConfigGovernor(
        String.join("/", faicConfigManager.importPath(), faicConfigManager.gcsConfigPath()),
        faicConfigManager.rootConfigName());
  }
}
