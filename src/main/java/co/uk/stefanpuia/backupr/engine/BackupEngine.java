package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.source.SourceHandlerFactory;
import co.uk.stefanpuia.backupr.transformers.TransformerFactory;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
    log.debug("Beginning backup process");
    config
        .sources()
        .forEach(
            source -> {
              final var sourceFiles = discoverSources(source);
              if (sourceFiles == null) return;
              final var remoteFiles = getTransformedFiles(dry, source, sourceFiles);
              uploadToRemote(dry, config, source, remoteFiles);
            });
    log.debug("Backup process completed");
  }

  private Set<File> discoverSources(final ConfigSource source) {
    log.debug("Backing up source '{}'", source.name());
    final var sourceFiles = sourceHandlerFactory.getInstance(source).getFiles();

    if (sourceFiles.isEmpty()) {
      log.warn("No files found for source '{}'", source.name());
      return null;
    }
    log.debug("Found {} source files:", sourceFiles.size());
    logFiles(sourceFiles);
    return sourceFiles;
  }

  private Set<File> getTransformedFiles(
      final boolean dry, final ConfigSource source, final Set<File> sourceFiles) {
    Set<File> transformedFiles = new HashSet<>(sourceFiles);
    for (final var transformerType : source.transformers()) {
      final var transformer = transformerFactory.getInstance(transformerType);
      log.debug("Transforming using '{}' transformer", transformerType);

      if (!dry) {
        transformedFiles = transformer.transform(source, transformedFiles);
      }
      log.debug("Transformed to {} files {}", transformedFiles.size(), transformedFiles);
    }
    return transformedFiles;
  }

  private void uploadToRemote(
      final boolean dry,
      final BackuprConfig config,
      final ConfigSource source,
      final Set<File> remoteFiles) {
    log.debug("Backing up {} files:", remoteFiles.size());
    logFiles(remoteFiles);
    source
        .remotes(config)
        .forEach(
            remote -> {
              log.debug("Backing up to remote '{}'", remote.name());
              if (!dry) {
                remoteHandlerFactory.getInstance(remote).upload(source, remoteFiles);
              }
              log.debug("Finished backup to remote '{}'", remote.name());
            });
  }

  private void logFiles(final Collection<File> files) {
    files.stream().map(File::toString).forEach(log::debug);
  }
}
