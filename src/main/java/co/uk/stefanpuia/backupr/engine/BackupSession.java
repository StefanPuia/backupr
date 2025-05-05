package co.uk.stefanpuia.backupr.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BackupSession implements DisposableBean {
  @Getter private final UUID instanceId = UUID.randomUUID();
  private Path backupRootPath;

  public Path getBackupRootPath() throws IOException {
    if (backupRootPath == null) {
      backupRootPath = Files.createTempDirectory("backupr-");
      log.debug("Created temporary root directory: '{}'", backupRootPath);
    }
    return backupRootPath;
  }

  @Override
  public void destroy() {
    if (backupRootPath != null) {
      final var backupRootDir = backupRootPath.toFile();
      log.debug("Deleting temporary root directory: '{}'", backupRootDir);
      try {
        FileUtils.forceDelete(backupRootDir);
      } catch (final IOException e) {
        log.warn("Could not delete temporary root directory: '{}'", backupRootDir, e);
      }
      backupRootPath = null;
    }
  }
}
