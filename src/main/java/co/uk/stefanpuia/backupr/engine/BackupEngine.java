package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.engine.source.SourceHandlerFactory;
import co.uk.stefanpuia.backupr.engine.transformers.TransformerFactory;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
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

  public void execute(final boolean dry, final BackuprConfig config) {
    log.info(dry ? "Beginning backup process (dry)" : "Beginning backup process");
    config.sources().stream()
        .filter(Predicate.not(ConfigSource::isEnabled))
        .map(ConfigSource::getName)
        .forEach(source -> log.debug("Ignoring source '{}' because it is disabled", source));
    config.sources().stream()
        .filter(ConfigSource::isEnabled)
        .forEach(
            source -> {
              final var sourceFiles = discoverSources(source);
              if (sourceFiles == null) return;
              final var remoteFiles = getTransformedFiles(dry, source, sourceFiles);
              uploadToRemote(dry, config, source, remoteFiles);
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

  private Set<File> getTransformedFiles(
      final boolean dry, final ConfigSource source, final Set<File> sourceFiles) {
    log.debug("Executing transformations");
    Set<File> transformedFiles = new HashSet<>(sourceFiles);
    for (final var transformerType : source.getTransformers()) {
      final var transformer = transformerFactory.getInstance(transformerType).setDry(dry);
      log.debug("Transforming using '{}' transformer", transformer.getType());

      transformedFiles = transformer.transform(source, transformedFiles);
      log.debug("Transformed to {} files:", transformedFiles.size());
      logFiles(transformedFiles);
    }
    return transformedFiles;
  }

  private void uploadToRemote(
      final boolean dry,
      final BackuprConfig config,
      final ConfigSource source,
      final Set<File> remoteFiles) {
    log.debug("Backing up {} files to remotes:", remoteFiles.size());
    logFiles(remoteFiles);
    source.getRemotes().stream()
        .filter(Predicate.not(ConfigRemote::isEnabled))
        .map(ConfigRemote::getName)
        .forEach(remote -> log.debug("Ignoring remote '{}' because it is disabled", remote));
    source.getRemotes().stream()
        .filter(ConfigRemote::isEnabled)
        .forEach(
            remote -> {
              log.debug("Backing up to {} remote '{}'", remote.getType(), remote.getName());
              remoteHandlerFactory.getInstance(remote).setDry(dry).upload(source, remoteFiles);
              log.debug("Finished backup to {} remote '{}'", remote.getType(), remote.getName());
            });
  }

  private void logFiles(final Collection<File> files) {
    files.stream().map(File::toString).sorted().forEach(log::debug);
  }
}
