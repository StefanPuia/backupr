package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.engine.state.BackupStateRecord;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateDto;
import jakarta.annotation.Nullable;
import java.io.File;
import java.util.Set;

public interface RemoteHandler {
  BackupStateRecord upload(ConfigSource source, Set<File> files);

  void cleanup(ConfigSource source, @Nullable BackupState backupState);

  boolean exists(String filePath);

  void delete(String filePath);

  BackupStateDto readStateFile(String stateFilePath);

  void writeStateFile(String stateFilePath, BackupStateDto state);
}
