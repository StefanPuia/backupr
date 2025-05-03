package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LocalRemoteHandler extends AbstractRemoteHandler {
  private final LocalConfigRemote remote;
  private final BackupHelper backupHelper;

  @Override
  public void upload(final ConfigSource source, final Set<File> files) {
    try {
      final var root = ensureRootDirectory(source);
      final var target = createTargetDirectory(root, backupHelper.generateBackupName());

      for (final var file : files) {
        final var targetPath =
            Path.of(target.getAbsolutePath())
                .resolve(backupHelper.getRelativePathIncludingFilename(source.getBasePath(), file));
        log.debug("Backing up '{}' to '{}'", file, targetPath);
        if (isDryRun()) continue;

        backupHelper.mkdirp(
            targetPath.getParent(),
            "Could not create parent directory: '%s'".formatted(targetPath.getParent()));
        Files.copy(file.toPath(), targetPath);
      }
    } catch (final IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private File ensureRootDirectory(final ConfigSource source) {
    final var dir = Path.of(remote.getLocation(), source.getName()).toFile();
    log.debug("Verifying root directory '{}'", dir);
    if (isDryRun()) return dir;

    if (!dir.exists()) {
      log.debug("Root directory not found, trying to create");
      backupHelper.mkdirp(
          dir.toPath(), "Could not create target directory: '%s'".formatted(remote.getLocation()));
    }
    if (!dir.isDirectory()) {
      throw new RemoteHandlerException(
          "Target location is not a directory: '%s'".formatted(remote.getLocation()));
    }
    return dir;
  }

  private File createTargetDirectory(final File root, final String backupName) {
    final var dir = Path.of(root.getAbsolutePath(), backupName).toFile();
    log.debug("Verifying backup directory '{}'", dir);
    if (isDryRun()) return dir;

    if (!dir.exists()) {
      log.debug("Backup directory not found, trying to create");
      backupHelper.mkdirp(
          dir.toPath(), "Could not create backup directory: '%s'".formatted(remote.getLocation()));
    } else {
      throw new RemoteHandlerException(
          "Backup directory already exists: '%s'".formatted(remote.getLocation()));
    }
    if (!dir.isDirectory()) {
      throw new RemoteHandlerException(
          "Backup location is not a directory: '%s'".formatted(remote.getLocation()));
    }
    return dir;
  }
}
