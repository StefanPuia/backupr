package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BackupHelper {
  private final BackupSession backupSession;

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
    final var filePath = file.toPath();
    try {
      if (filePath.startsWith(basePath)) {
        return basePath.relativize(filePath);
      } else {
        return filePath.getFileName();
      }
    } catch (SecurityException | IllegalArgumentException e) {
      return filePath.getFileName();
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

  public Path createTempDirectory(final String prefix) throws IOException {
    return Files.createTempDirectory(backupSession.getBackupRootPath(), "%s-".formatted(prefix));
  }
}
