package co.uk.stefanpuia.backupr.remote;

import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LocalRemoteHandler extends AbstractRemoteHandler {
  private final LocalConfigRemote remote;

  @Override
  public void upload(final ConfigSource source, final Set<File> files) {
    try {
      final var root = ensureRootDirectory(source);
      final var target = createTargetDirectory(root, generateBackupName());

      for (final var file : files) {
        final var targetPath = Path.of(target.getAbsolutePath(), getRelativePath(source, file));
        log.debug("Backing up '{}' to '{}'", file, targetPath);
        if (isDryRun()) continue;

        mkdirp(targetPath.getParent(), "Could not create parent directory: '%s'");
        Files.copy(file.toPath(), targetPath);
      }
    } catch (final IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private String getRelativePath(final ConfigSource source, final File file) {
    try {
      return source.getBasePath().relativize(file.toPath()).toString();
    } catch (IllegalArgumentException e) {
      return file.getName();
    }
  }

  private File ensureRootDirectory(final ConfigSource source) {
    final var dir = Path.of(remote.location(), source.name()).toFile();
    log.debug("Verifying root directory '{}'", dir);
    if (isDryRun()) return dir;

    if (!dir.exists()) {
      log.debug("Root directory not found, trying to create");
      mkdirp(dir.toPath(), "Could not create target directory: '%s'");
    }
    if (!dir.isDirectory()) {
      throw new RemoteHandlerException(
          "Target location is not a directory: '%s'".formatted(remote.location()));
    }
    return dir;
  }

  private File createTargetDirectory(final File root, final String backupName) {
    final var dir = Path.of(root.getAbsolutePath(), backupName).toFile();
    log.debug("Verifying backup directory '{}'", dir);
    if (isDryRun()) return dir;

    if (!dir.exists()) {
      log.debug("Backup directory not found, trying to create");
      mkdirp(dir.toPath(), "Could not create backup directory: '%s'");
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

  private void mkdirp(final Path path, final String exceptionMessage) {
    final var directory = path.toFile();
    if (!directory.exists()) {
      final var created = directory.mkdirs();
      if (!created) {
        throw new RemoteHandlerException(exceptionMessage.formatted(remote.location()));
      }
    }
  }
}
