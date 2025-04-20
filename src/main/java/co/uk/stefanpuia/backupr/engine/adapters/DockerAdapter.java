package co.uk.stefanpuia.backupr.engine.adapters;

import co.uk.stefanpuia.backupr.engine.source.SourceHandlerException;
import com.github.dockerjava.api.DockerClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DockerAdapter {
  private final DockerClientProvider dockerClientProvider;

  public DockerClient getDockerClient() {
    return dockerClientProvider.getDockerClient();
  }

  public void copyPathFromContainer(
      final String containerId, final String containerPath, final Path targetHostPath)
      throws IOException {
    try (final var copyCmd =
            dockerClientProvider
                .getDockerClient()
                .copyArchiveFromContainerCmd(containerId, containerPath);
        final var fileStream = copyCmd.withHostPath(targetHostPath.toString()).exec();
        final var tais = new TarArchiveInputStream(fileStream)) {
      TarArchiveEntry entry;

      while ((entry = tais.getNextEntry()) != null) {
        final var entryPath = targetHostPath.resolve(entry.getName());

        if (entry.isDirectory()) {
          Files.createDirectories(entryPath);
        } else {
          final var outputFile = entryPath.toFile();
          Files.createDirectories(outputFile.getParentFile().toPath());
          try {
            Files.copy(tais, outputFile.toPath());
          } catch (IOException e) {
            throw new SourceHandlerException(e);
          }
        }
      }
    }
  }

  public void ensureContainer(final String containerId) {
    // Trying to find container first to ensure isAllowNotFoundPaths only checks for paths
    final var container = getDockerClient().inspectContainerCmd(containerId).exec();
    log.debug("Found container '{}' ({})", container.getName(), container.getId());
  }
}
