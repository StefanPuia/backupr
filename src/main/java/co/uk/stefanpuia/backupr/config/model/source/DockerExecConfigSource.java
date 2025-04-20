package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import java.nio.file.Path;
import java.util.List;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class DockerExecConfigSource implements ConfigSource {

  public abstract String getContainer();

  public abstract List<Command> getCommands();

  public Path getBasePath() {
    return Path.of("");
  }

  public abstract long getTimeoutInSeconds();

  public record Command(String outputFile, String[] command) {}
}
