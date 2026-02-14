package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class LocalConfigRemote implements ConfigRemote, CleanupRemote {
  public abstract String getLocation();

  @Override
  public RemoteType getType() {
    return RemoteType.LOCAL;
  }
}
