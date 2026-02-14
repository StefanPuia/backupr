package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import co.uk.stefanpuia.backupr.engine.state.BackupStateRecord;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LocalRemoteHandler implements RemoteHandler {
  private final LocalConfigRemote remote;
  private final BackupHelper backupHelper;
  private final BackupSession backupSession;
  private final ObjectMapper jsonMapper;

  @Override
  public BackupStateRecord upload(final ConfigSource source, final Set<File> files) {
    try {
      final var root = ensureRootDirectory(source);
      final var rootPath = root.toPath();
      final var target = createTargetDirectory(root, backupHelper.generateBackupName());
      final var targetPaths = new ArrayList<String>();

      for (final var file : files) {
        final var targetPath =
            Path.of(target.getAbsolutePath())
                .resolve(backupHelper.getRelativePathIncludingFilename(source.getBasePath(), file));
        log.debug("Backing up '{}' to '{}'", file, targetPath);
        if (backupSession.isDry()) continue;

        backupHelper.mkdirp(
            targetPath.getParent(),
            "Could not create parent directory: '%s'".formatted(targetPath.getParent()));
        Files.copy(file.toPath(), targetPath);
        targetPaths.add(backupHelper.toString(rootPath.relativize(targetPath)));
      }

      return new BackupStateRecord(source.getName(), remote.getName(), targetPaths);
    } catch (final IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private File ensureRootDirectory(final ConfigSource source) {
    final var dir = Path.of(remote.getLocation(), source.getName()).toFile();
    log.debug("Verifying root directory '{}'", dir);
    if (backupSession.isDry()) return dir;

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
    if (backupSession.isDry()) return dir;

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

  @Override
  public void cleanup(final ConfigSource source, @Nullable final BackupState backupState) {
    final var root = ensureRootDirectory(source).toPath();
    final var cleanupFiles =
        backupHelper.getRemoteRelativeFilePathsToCleanup(remote, source, backupState);
    log.debug("Cleaning up {} files from Local remote:", cleanupFiles.size());
    for (final var filePath : cleanupFiles) {
      log.debug(filePath);
    }
    if (backupSession.isDry()) return;
    cleanupFiles.parallelStream()
        .map(root::resolve)
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .forEach(this::delete);
  }

  @Override
  public boolean exists(final String filePath) {
    return Path.of(remote.getLocation(), filePath).toFile().exists();
  }

  @Override
  public void delete(final String filePath) {
    try {
      Files.delete(Path.of(filePath));
    } catch (IOException ignored) {
      log.warn("Could not delete file '{}'", filePath);
    }
  }

  @Override
  public BackupStateDto readStateFile(final String stateFilePath) {
    if (!exists(stateFilePath)) {
      return null;
    }
    try {
      return jsonMapper.readValue(
          Path.of(remote.getLocation(), stateFilePath).toFile(), BackupStateDto.class);
    } catch (IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  @Override
  public void writeStateFile(final String stateFilePath, final BackupStateDto state) {
    try {
      final var targetPath = Path.of(remote.getLocation(), stateFilePath);
      backupHelper.mkdirp(
          targetPath.getParent(),
          "Could not create parent directory: '%s'".formatted(targetPath.getParent()));
      final var tempFile = Files.createTempFile(targetPath.getParent(), "backupr", ".tmp");
      tempFile.toFile().deleteOnExit();
      Files.writeString(tempFile, jsonMapper.writeValueAsString(state), StandardCharsets.UTF_8);
      Files.move(
          tempFile,
          targetPath,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RemoteHandlerException(e);
    }
  }
}
