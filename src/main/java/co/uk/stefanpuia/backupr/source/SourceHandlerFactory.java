package co.uk.stefanpuia.backupr.source;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SourceHandlerFactory {
  public SourceHandler getInstance(final ConfigSource source, final BackuprConfig config) {
    return switch (source) {
      case LocalConfigSource localSource -> new LocalSourceHandler(localSource, config);
      default -> throw new IllegalArgumentException("Unsupported config source: " + source);
    };
  }
}
