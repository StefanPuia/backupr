package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.remote.mapper.AzureCredentialMapper;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
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
  private final AzureCredentialMapper credentialMapper;
  private final BackupHelper backupHelper;
  private final StringTemplateRenderer stringTemplateRenderer;

  @Override
  public void upload(final ConfigSource source, final Set<File> files) {
    final var containerClient = buildClient();
    for (final var file : files) {
      final var targetPath = "%s/%s".formatted(getPrefix(source, file), file.getName());
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
      blobClient.upload(fileInputStream, remote.isOverwrite());

      final var headers = new BlobHttpHeaders();
      headers.setContentType(getFileContentType(fileInputStream, file));
      blobClient.setHttpHeaders(headers);
    } catch (IOException e) {
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

  private BlobContainerClient buildClient() {
    log.debug("Using credential '{}'", remote.getCredentials().getName());
    final var credential = credentialMapper.convert(remote.getCredentials());
    return new BlobServiceClientBuilder()
        .endpoint(remote.getEndpoint())
        .credential(credential)
        .buildClient()
        .getBlobContainerClient(remote.getContainer());
  }

  private String getPrefix(final ConfigSource source, final File file) {
    final var originalFilePath = backupHelper.getRelativePath(source.getBasePath(), file);
    return stringTemplateRenderer
        .applyTemplate(
            remote.getBlobPrefixPattern(),
            remote
                .getVariables()
                .withContext(
                    Map.of("sourceName", source.getName(), "originalFilePath", originalFilePath)))
        .replaceFirst("^/*", "")
        .replaceFirst("/*$", "");
  }
}
