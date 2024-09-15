package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.source.SourceHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BackupEngine {
  private final SourceHandlerFactory sourceHandlerFactory;
  private final RemoteHandlerFactory remoteHandlerFactory;

  public void execute(final BackuprConfig config) {
    log.info("Beginning backup");
    config
        .sources()
        .forEach(
            source -> {
              log.info("Backing up source '{}'", source.name());
              final var files = sourceHandlerFactory.getInstance(source, config).getFiles();

              if (files.isEmpty()) {
                log.warn("No files found for source '{}'", source.name());
                return;
              }

              log.info("Backing up files '{}'", files);
              source
                  .remotes(config)
                  .forEach(
                      remote -> {
                        log.info("Backing up to remote '{}'", remote.name());
                        remoteHandlerFactory.getInstance(remote).upload(files);
                      });
            });
  }
}
