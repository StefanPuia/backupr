package co.uk.stefanpuia.backupr.engine.source;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.DockerCpConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.DockerExecConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.adapters.DockerAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SourceHandlerFactory {
  private final DockerAdapter dockerAdapter;
  private final BackupHelper backupHelper;

  public SourceHandler getInstance(final ConfigSource source) {
    return switch (source) {
      case LocalConfigSource localSource -> new LocalSourceHandler(localSource);
      case DockerCpConfigSource dockerCpSource ->
          new DockerCpSourceHandler(dockerCpSource, backupHelper, dockerAdapter);
      case DockerExecConfigSource dockerExecSource ->
          new DockerExecSourceHandler(dockerExecSource, backupHelper, dockerAdapter);
      default -> throw new IllegalArgumentException("Unsupported config source: " + source);
    };
  }
}
