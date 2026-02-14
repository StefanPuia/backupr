package co.uk.stefanpuia.backupr.config.model.state;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import java.time.Instant;
import java.util.List;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface BackupStateBackup {
  String getSourceName();

  Instant getTimestamp();

  List<String> getRemoteRelativeFilePaths();

  boolean isOverwrites();
}
