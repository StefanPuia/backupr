package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;

@Component
public class BackupHelper {

  public void mkdirp(final Path path, final String exceptionMessage) {
    final var directory = path.toFile();
    if (!directory.exists()) {
      final var created = directory.mkdirs();
      if (!created) {
        throw new RemoteHandlerException(exceptionMessage);
      }
    }
  }

  public Path getRelativePathIncludingFilename(final Path basePath, final File file) {
    try {
      return basePath.relativize(file.toPath());
    } catch (IllegalArgumentException e) {
      return Path.of(file.getName());
    }
  }

  public String toString(final Path path) {
    return StreamSupport.stream(path.spliterator(), false)
        .map(Path::toString)
        .collect(Collectors.joining("/"));
  }

  public String generateBackupName() {
    return LocalDateTime.now().withNano(0).toString().replaceAll("\\W", "-");
  }
}
