package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.model.state.BackupStateBackup;
import co.uk.stefanpuia.backupr.engine.state.BackupStateRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BackupSession implements DisposableBean {
  @Getter private final UUID instanceId = UUID.randomUUID();
  private final AtomicReference<Path> backupRootPath = new AtomicReference<>();
  private final AtomicBoolean dryRun = new AtomicBoolean(false);
  private final List<BackupStateRecord> backups = new ArrayList<>();
  private final List<BackupStateBackup> cleanups = new ArrayList<>();

  public synchronized Path getBackupRootPath() throws IOException {
    if (backupRootPath.get() == null) {
      backupRootPath.set(Files.createTempDirectory("backupr-"));
      log.debug("Created temporary root directory: '{}'", backupRootPath);
    }
    return backupRootPath.get();
  }

  @Override
  public void destroy() {
    if (backupRootPath.get() != null) {
      final var backupRootDir = backupRootPath.get().toFile();
      log.debug("Deleting temporary root directory: '{}'", backupRootDir);
      try {
        FileUtils.forceDelete(backupRootDir);
      } catch (final IOException e) {
        log.warn("Could not delete temporary root directory: '{}'", backupRootDir, e);
      }
      backupRootPath.set(null);
    }
  }

  public synchronized boolean isDry() {
    return dryRun.get();
  }

  public synchronized void setDry(boolean dry) {
    dryRun.set(dry);
  }

  public synchronized void addBackup(final BackupStateRecord backup) {
    backups.add(backup);
  }

  public synchronized List<BackupStateRecord> getBackups() {
    return backups;
  }

  public synchronized void addCleanup(final List<BackupStateBackup> removable) {
    cleanups.addAll(removable);
  }

  public synchronized List<BackupStateBackup> getCleanups() {
    return cleanups;
  }
}
