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

  public void execute(final boolean dry, final BackuprConfig config) {
    log.debug("Beginning backup process");
    config
        .sources()
        .forEach(
            source -> {
              log.debug("Backing up source '{}'", source.name());
              final var files = sourceHandlerFactory.getInstance(source).getFiles();

              if (files.isEmpty()) {
                log.warn("No files found for source '{}'", source.name());
                return;
              }

              log.debug("Backing up files '{}'", files);
              source
                  .remotes(config)
                  .forEach(
                      remote -> {
                        log.debug("Backing up to remote '{}'", remote.name());
                        if (!dry) {
                          remoteHandlerFactory.getInstance(remote).upload(files);
                        }
                      });
            });
    log.debug("Backup process completed");
  }
}
