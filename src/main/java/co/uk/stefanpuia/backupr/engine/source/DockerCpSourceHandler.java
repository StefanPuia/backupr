package co.uk.stefanpuia.backupr.engine.source;

import co.uk.stefanpuia.backupr.config.model.source.DockerCpConfigSource;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
@AllArgsConstructor
public class DockerCpSourceHandler implements SourceHandler {
  private static final String DEFAULT_DOCKER_HOST = "unix:///var/run/docker.sock";
  private static final String WINDOWS_DEFAULT_DOCKER_HOST = "npipe:////./pipe/docker_engine";
  private final DockerCpConfigSource source;

  @Override
  public Set<File> getFiles() {
    final Set<File> sourcedFiles = new HashSet<>();
    final var httpClient = buildHttpClient();
    try (final var dockerClient = buildDockerClient(httpClient)) {
      // Trying to find container first to ensure isAllowNotFoundPaths only checks for paths
      final var container = dockerClient.inspectContainerCmd(source.getContainer()).exec();
      log.debug("Found container '{}' ({})", container.getName(), container.getId());

      final var directory = createTempTargetDir();
      for (final var path : source.getPaths()) {
        log.debug("Sourcing path: {}:{}", source.getContainer(), path);
        try (final var fileStream =
            dockerClient
                .copyArchiveFromContainerCmd(source.getContainer(), path)
                .withHostPath(directory.toString())
                .exec()) {
          extractTarArchive(fileStream, directory);
        } catch (final NotFoundException notFoundException) {
          if (!source.isAllowNotFoundPaths()) {
            throw notFoundException;
          } else {
            log.info("Skipping non-existent path: {}:{}", source.getContainer(), path);
          }
        }
      }

      FileUtils.iterateFiles(directory.toFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
          .forEachRemaining(sourcedFiles::add);
      return sourcedFiles;
    } catch (IOException | DockerException e) {
      throw new SourceHandlerException(e);
    }
  }

  public void extractTarArchive(final InputStream tarInputStream, final Path outputPath)
      throws IOException {

    try (final var tais = new TarArchiveInputStream(tarInputStream)) {
      TarArchiveEntry entry;

      while ((entry = tais.getNextEntry()) != null) {
        final var entryPath = outputPath.resolve(entry.getName());

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

  private DockerClient buildDockerClient(final DockerHttpClient httpClient) {
    return DockerClientImpl.getInstance(
        DefaultDockerClientConfig.createDefaultConfigBuilder().build(), httpClient);
  }

  private DockerHttpClient buildHttpClient() {
    return new ZerodepDockerHttpClient.Builder()
        .dockerHost(
            URI.create(
                SystemUtils.IS_OS_WINDOWS ? WINDOWS_DEFAULT_DOCKER_HOST : DEFAULT_DOCKER_HOST))
        .build();
  }

  private Path createTempTargetDir() throws IOException {
    log.debug("Creating temporary directory");
    return Files.createTempDirectory("docker-cp-temp");
  }
}
