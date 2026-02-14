package co.uk.stefanpuia.backupr.config.model.state;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import java.util.List;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface BackupStateRemote {
  String getName();

  List<BackupStateBackup> getBackups();
}
