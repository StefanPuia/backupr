package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import co.uk.stefanpuia.backupr.engine.adapters.AzureBlobClientProvider;
import co.uk.stefanpuia.backupr.engine.state.BackupStateRecord;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateDto;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("StorageUploadWithoutLengthCheck")
@Slf4j
@AllArgsConstructor
public class AzureStorageBlobRemoteHandler implements RemoteHandler {
  private final AzureStorageBlobConfigRemote remote;
  private final AzureBlobClientProvider azureBlobClientProvider;
  private final BackupHelper backupHelper;
  private final StringTemplateRenderer stringTemplateRenderer;
  private final BackupSession backupSession;
  private final ObjectMapper jsonMapper;

  private BlobContainerClient buildContainerClient() {
    return azureBlobClientProvider.buildClient(
        remote.getCredentials(), remote.getEndpoint(), remote.getContainer());
  }

  @Override
  public BackupStateRecord upload(final ConfigSource source, final Set<File> files) {
    final var containerClient = buildContainerClient();
    final var targetPaths = new ArrayList<String>();
    for (final var file : files) {
      final var targetPath =
          "%s/%s"
              .formatted(
                  getPrefix(source, file),
                  backupHelper.toString(
                      backupHelper.getRelativePathIncludingFilename(source.getBasePath(), file)));
      log.debug(
          "Backing up '{}' to '{}/{}/{}'",
          file,
          remote.getEndpoint().replaceFirst("/*$", ""),
          remote.getContainer(),
          targetPath);
      if (backupSession.isDry()) continue;
      uploadFileToBlob(file, containerClient, targetPath);
      targetPaths.add(targetPath);
    }

    return new BackupStateRecord(
        source.getName(), remote.getName(), targetPaths, remote.isOverwrite());
  }

  @Override
  public void cleanup(final ConfigSource source, @Nullable final BackupState backupState) {
    final var cleanupFiles =
        backupHelper.getRemoteRelativeFilePathsToCleanup(remote, source, backupState);
    log.debug("Cleaning up {} files from AzureBlob remote:", cleanupFiles.size());
    for (final var filePath : cleanupFiles) {
      log.debug(filePath);
    }
    if (backupSession.isDry()) return;
    cleanupFiles.parallelStream().forEach(this::delete);
  }

  @Override
  public boolean exists(final String filePath) {
    return buildContainerClient().getBlobClient(filePath).exists();
  }

  @Override
  public void delete(final String filePath) {
    buildContainerClient().getBlobClient(filePath).deleteIfExists();
  }

  @Override
  public BackupStateDto readStateFile(final String stateFilePath) {
    if (!exists(stateFilePath)) {
      return null;
    }
    try {
      return jsonMapper.readValue(
          buildContainerClient().getBlobClient(stateFilePath).downloadContent().toBytes(),
          BackupStateDto.class);
    } catch (IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  @Override
  public void writeStateFile(final String stateFilePath, final BackupStateDto state) {
    try {
      buildContainerClient()
          .getBlobClient(stateFilePath)
          .upload(BinaryData.fromBytes(jsonMapper.writeValueAsBytes(state)), true);
    } catch (JsonProcessingException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private void uploadFileToBlob(
      final File file, final BlobContainerClient containerClient, final String targetPath) {
    try (final var fileInputStream = new FileInputStream(file)) {
      final var blobClient = containerClient.getBlobClient(targetPath);
      blobClient.upload(fileInputStream, file.length(), remote.isOverwrite());

      final var headers = new BlobHttpHeaders();
      headers.setContentType(getFileContentType(fileInputStream, file));
      blobClient.setHttpHeaders(headers);
    } catch (BlobStorageException | IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private String getFileContentType(final FileInputStream fileInputStream, final File file) {
    try {
      final var fromName = URLConnection.guessContentTypeFromName(file.getName());
      if (fromName != null) return fromName;
      return URLConnection.guessContentTypeFromStream(new BufferedInputStream(fileInputStream));
    } catch (IOException ignored) {
      return null;
    }
  }

  private String getPrefix(final ConfigSource source, final File file) {
    final var originalFilePath =
        backupHelper.toString(
            backupHelper.getRelativePathIncludingFilename(source.getBasePath(), file));
    return stringTemplateRenderer
        .applyTemplate(
            remote.getBlobPrefixPattern(),
            remote
                .getVariables()
                .withContext(
                    Map.of("sourceName", source.getName(), "originalFilePath", originalFilePath)))
        // remove leading slashes
        .replaceFirst("^/*", "")
        // remove trailing slashes
        .replaceFirst("/*$", "");
  }
}
