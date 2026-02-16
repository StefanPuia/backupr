package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import java.nio.file.Path;
import java.util.List;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class DockerCpConfigSource implements ConfigSource {

  public abstract String getContainer();

  public abstract List<String> getPaths();

  public abstract Boolean isAllowNotFoundPaths();

  public abstract Path getBasePath();
}
