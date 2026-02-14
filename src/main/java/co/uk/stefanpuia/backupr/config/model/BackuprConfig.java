package co.uk.stefanpuia.backupr.config.model;

import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.util.List;
import org.immutables.value.Value;

@Valid
@ModelStyle
@Value.Immutable
public abstract class BackuprConfig {
  @Nullable
  @Value.Parameter
  public abstract BackupState getState();

  @Value.Parameter
  public abstract List<@Valid ConfigRemote> getRemotes();

  @Value.Parameter
  public abstract List<@Valid ConfigSource> getSources();
}
