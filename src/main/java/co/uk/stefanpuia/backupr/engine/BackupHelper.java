package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.remote.RemoteHandlerException;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
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

  public String getRelativePath(final Path basePath, final File file) {
    try {
      return basePath.relativize(file.toPath()).toString();
    } catch (IllegalArgumentException e) {
      return file.getName();
    }
  }

  public String generateBackupName() {
    return LocalDateTime.now().withNano(0).toString().replaceAll("\\W", "-");
  }
}
