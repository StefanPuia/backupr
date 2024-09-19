package co.uk.stefanpuia.backupr.remote;

import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LocalRemoteHandler implements RemoteHandler {
  private final LocalConfigRemote remote;

  @Override
  public void upload(final ConfigSource source, final List<File> files) {
    try {
      final var root = ensureRootDirectory(source);
      final var target = createTargetDirectory(root, generateBackupName());

      for (final var file : files) {
        final var targetPath = Path.of(target.getAbsolutePath(), file.getName());
        log.debug("Backing up '{}' to '{}'", file.getName(), targetPath);
        Files.copy(file.toPath(), targetPath);
      }
    } catch (final IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private File ensureRootDirectory(final ConfigSource source) {
    final var path = Path.of(remote.location(), source.name());
    log.debug("Verifying root directory '{}'", path);
    final var dir = path.toFile();
    if (!dir.exists()) {
      log.debug("Root directory not found, trying to create");
      final var created = dir.mkdirs();
      if (!created) {
        throw new RemoteHandlerException(
            "Could not create target directory: '%s'".formatted(remote.location()));
      }
    }
    if (!dir.isDirectory()) {
      throw new RemoteHandlerException(
          "Target location is not a directory: '%s'".formatted(remote.location()));
    }
    return dir;
  }

  private File createTargetDirectory(final File root, final String backupName) {
    final var backupDirPath = Path.of(root.getAbsolutePath(), backupName);
    log.debug("Verifying backup directory '{}'", backupDirPath);
    final var dir = new File(backupDirPath.toString());
    if (!dir.exists()) {
      log.debug("Backup directory not found, trying to create");
      final var created = dir.mkdirs();
      if (!created) {
        throw new RemoteHandlerException(
            "Could not create backup directory: '%s'".formatted(remote.location()));
      }
    } else {
      throw new RemoteHandlerException(
          "Backup directory already exists: '%s'".formatted(remote.location()));
    }
    if (!dir.isDirectory()) {
      throw new RemoteHandlerException(
          "Backup location is not a directory: '%s'".formatted(remote.location()));
    }
    return dir;
  }

  private String generateBackupName() {
    return LocalDateTime.now().withNano(0).toString().replaceAll("\\W", "-");
  }
}
