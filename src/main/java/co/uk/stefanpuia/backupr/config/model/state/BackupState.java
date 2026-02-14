package co.uk.stefanpuia.backupr.config.model.state;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.Map;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class BackupState {
  public static final String STATE_FILE_NAME = ".backupr.state.json";

  @Value.Parameter
  public abstract ConfigRemote getRemote();

  @Nullable
  public abstract Instant getLastBackup();

  public abstract Map<String, BackupStateRemote> getRemotes();
}
