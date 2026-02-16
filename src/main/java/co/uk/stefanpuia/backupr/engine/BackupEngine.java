package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.CleanupRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.engine.source.SourceHandlerFactory;
import co.uk.stefanpuia.backupr.engine.transformers.TransformerFactory;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BackupEngine {
  private final SourceHandlerFactory sourceHandlerFactory;
  private final TransformerFactory transformerFactory;
  private final RemoteHandlerFactory remoteHandlerFactory;
  private final BackupSession backupSession;

  public void execute(final BackuprConfig config) {
    log.info(backupSession.isDry() ? "Beginning backup process (dry)" : "Beginning backup process");
    config
        .getSources()
        .forEach(
            source -> {
              if (!source.isEnabled()) {
                log.debug("Ignoring source '{}' because it is disabled", source.getName());
                return;
              }
              if (source.getRemotes().isEmpty()) {
                log.debug(
                    "Ignoring source '{}' because it does not have any remotes", source.getName());
                return;
              }

              final var sourceFiles = discoverSources(source);
              if (sourceFiles == null) return;
              final var remoteFiles = getTransformedFiles(source, sourceFiles);
              uploadToRemote(config, source, remoteFiles);
              cleanUpRemote(config, source);
            });
    log.info("Backup process completed");
  }

  private Set<File> discoverSources(final ConfigSource source) {
    log.debug("Backing up source '{}'", source.getName());
    final var sourceFiles = sourceHandlerFactory.getInstance(source).getFiles();

    if (sourceFiles.isEmpty()) {
      log.warn("No files found for source '{}'", source.getName());
      return null;
    }
    log.debug("Found {} source files:", sourceFiles.size());
    logFiles(sourceFiles);
    return sourceFiles;
  }

  private Set<File> getTransformedFiles(final ConfigSource source, final Set<File> sourceFiles) {
    log.debug("Executing transformations");
    Set<File> transformedFiles = new HashSet<>(sourceFiles);
    for (final var transformerType : source.getTransformers()) {
      final var transformer = transformerFactory.getInstance(transformerType);
      log.debug("Transforming using '{}' transformer", transformer.getType());

      transformedFiles = transformer.transform(source, transformedFiles);
      log.debug("Transformed {} files:", transformedFiles.size());
      logFiles(transformedFiles);
    }
    return transformedFiles;
  }

  private void uploadToRemote(
      final BackuprConfig config, final ConfigSource source, final Set<File> remoteFiles) {
    log.debug("Backing up {} files to remotes:", remoteFiles.size());
    logFiles(remoteFiles);
    logDisabledSourceRemotes(source.getRemotes());
    source.getRemotes().stream()
        .filter(ConfigRemote::isEnabled)
        .forEach(
            remote -> {
              log.debug("Backing up to {} remote '{}'", remote.getType(), remote.getName());
              final var record =
                  remoteHandlerFactory.getInstance(remote).upload(source, remoteFiles);
              backupSession.addBackup(record);
              log.debug("Finished backup to {} remote '{}'", remote.getType(), remote.getName());
            });
  }

  private void cleanUpRemote(final BackuprConfig config, final ConfigSource source) {
    log.debug("Cleaning up remotes for source: '{}'", source.getName());
    final var cleanupRemotes =
        source.getRemotes().stream().filter(remote -> remote instanceof CleanupRemote).toList();
    logDisabledSourceRemotes(cleanupRemotes);
    cleanupRemotes.stream()
        .filter(ConfigRemote::isEnabled)
        .forEach(
            remote -> {
              log.debug("Cleaning up remote '{}'", remote.getName());
              remoteHandlerFactory.getInstance(remote).cleanup(source, config.getState());
              log.debug("Finished cleaning remote '{}'", remote.getName());
            });
  }

  private void logDisabledSourceRemotes(final List<ConfigRemote> sourceRemotes) {
    sourceRemotes.stream()
        .filter(Predicate.not(ConfigRemote::isEnabled))
        .map(ConfigRemote::getName)
        .forEach(remote -> log.debug("Ignoring remote '{}' because it is disabled", remote));
  }

  private void logFiles(final Collection<File> files) {
    files.stream().map(File::toString).sorted().forEach(log::debug);
  }
}
