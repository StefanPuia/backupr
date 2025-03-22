package co.uk.stefanpuia.backupr.engine.source;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SourceHandlerFactory {
  public SourceHandler getInstance(final ConfigSource source) {
    return switch (source) {
      case LocalConfigSource localSource -> new LocalSourceHandler(localSource);
      default -> throw new IllegalArgumentException("Unsupported config source: " + source);
    };
  }
}
