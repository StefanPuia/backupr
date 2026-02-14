package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.model.cleanup.Cleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.KeepAllCleanup;
import co.uk.stefanpuia.backupr.config.model.cleanup.ParameterizedCleanup;
import co.uk.stefanpuia.backupr.config.model.remote.CleanupRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.config.model.state.BackupStateBackup;
import co.uk.stefanpuia.backupr.config.model.state.BackupStateRemote;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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

  public List<String> getRemoteRelativeFilePathsToCleanup(
      final CleanupRemote remote,
      final ConfigSource source,
      @Nullable final BackupState backupState) {
    if (backupState == null) {
      return List.of();
    }

    final var cleanup = getCleanup(remote, source);
    log.debug("Resolved cleanup policy: {}", cleanup);
    if (cleanup instanceof KeepAllCleanup) {
      return List.of();
    } else if (cleanup instanceof ParameterizedCleanup parameterizedCleanup) {
      final var stateBackups = getStateBackups(remote, source, backupState);
      final var removable = new ArrayList<BackupStateBackup>();
      final var now = Instant.now();
      for (int i = parameterizedCleanup.getKeepCount(); i < stateBackups.size(); i++) {
        final var backup = stateBackups.get(i);
        if (getDaysDifference(backup.getTimestamp(), now) > parameterizedCleanup.getKeepDays()) {
          removable.add(backup);
        }
      }
      backupSession.addCleanup(removable);
      return removable.stream()
          .map(BackupStateBackup::getRemoteRelativeFilePaths)
          .flatMap(List::stream)
          .toList();
    } else {
      throw new IllegalStateException("Unknown cleanup type: " + cleanup.getClass());
    }
  }

  private List<BackupStateBackup> getStateBackups(
      final CleanupRemote remote, final ConfigSource source, final BackupState backupState) {
    return Optional.ofNullable(backupState.getRemotes())
        .map(remotes -> remotes.get(remote.getName()))
        .map(BackupStateRemote::getBackups)
        .orElseGet(List::of)
        .stream()
        .filter(backup -> backup.getSourceName().equals(source.getName()))
        .sorted(Comparator.comparing(BackupStateBackup::getTimestamp))
        .toList()
        .reversed();
  }

  private Cleanup getCleanup(final CleanupRemote remote, final ConfigSource source) {
    return Stream.of(
            Optional.ofNullable(source.getCleanup()),
            Optional.ofNullable(remote.getCleanup()),
            Optional.of(KeepAllCleanup.create()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow();
  }

  private long getDaysDifference(final Instant backupStamp, final Instant now) {
    return Math.abs(ChronoUnit.DAYS.between(backupStamp, now));
  }
}
