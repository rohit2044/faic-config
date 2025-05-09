package org.example.faic.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.transfermanager.DownloadResult;
import com.google.cloud.storage.transfermanager.ParallelDownloadConfig;
import com.google.cloud.storage.transfermanager.TransferManager;
import java.nio.file.Path;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class GcsUtil {
  public void downloadFolder(
      Storage storage, TransferManager transferManager,
      String bucketName, String folderPath, String destinationDirectory) {

    var blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPath))
        .streamAll()
        .map(Blob::asBlobInfo)
        .toList();

    ParallelDownloadConfig parallelDownloadConfig =
        ParallelDownloadConfig.newBuilder()
            .setBucketName(bucketName)
            .setDownloadDirectory(Path.of(destinationDirectory))
            .build();

    List<DownloadResult> results =
        transferManager.downloadBlobs(blobs, parallelDownloadConfig).getDownloadResults();

    for (DownloadResult result : results) {
      log.info(
          "Download of " +
              result.getInput().getName() +
              " completed with status " +
              result.getStatus());
    }
  }
}
