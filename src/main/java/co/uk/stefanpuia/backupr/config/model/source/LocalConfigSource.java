package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import java.nio.file.Path;
import java.util.List;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class LocalConfigSource implements ConfigSource {

  public abstract String getDirectory();

  public abstract List<String> getFiles();

  public Path getBasePath() {
    return Path.of(getDirectory());
  }
}
