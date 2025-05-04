package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.adapters.AzureBlobClientProvider;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AzureStorageBlobRemoteHandler extends AbstractRemoteHandler {
  private final AzureStorageBlobConfigRemote remote;
  private final AzureBlobClientProvider azureBlobClientProvider;
  private final BackupHelper backupHelper;
  private final StringTemplateRenderer stringTemplateRenderer;

  @Override
  public void upload(final ConfigSource source, final Set<File> files) {
    final var containerClient =
        azureBlobClientProvider.buildClient(
            remote.getCredentials(), remote.getEndpoint(), remote.getContainer());
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
      if (isDryRun()) continue;
      uploadFileToBlob(file, containerClient, targetPath);
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
