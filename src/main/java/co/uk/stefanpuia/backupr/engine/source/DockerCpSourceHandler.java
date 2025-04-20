package co.uk.stefanpuia.backupr.engine.source;

import co.uk.stefanpuia.backupr.config.model.source.DockerCpConfigSource;
import co.uk.stefanpuia.backupr.engine.adapters.DockerAdapter;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

@Slf4j
@AllArgsConstructor
public class DockerCpSourceHandler implements SourceHandler {
  private final DockerCpConfigSource source;
  private final DockerAdapter dockerAdapter;

  @Override
  public Set<File> getFiles() {
    final Set<File> sourcedFiles = new HashSet<>();
    try {
      dockerAdapter.ensureContainer(source.getContainer());

      final var directory = createTempTargetDir();
      for (final var path : source.getPaths()) {
        log.debug("Sourcing path: {}:{}", source.getContainer(), path);

        try {
          dockerAdapter.copyPathFromContainer(source.getContainer(), path, directory);
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

  private Path createTempTargetDir() throws IOException {
    log.debug("Creating temporary directory");
    return Files.createTempDirectory("docker-cp-temp");
  }
}
