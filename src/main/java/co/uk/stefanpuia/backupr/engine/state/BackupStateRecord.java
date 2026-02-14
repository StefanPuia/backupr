package co.uk.stefanpuia.backupr.engine.state;

import java.time.Instant;
import java.util.List;

public record BackupStateRecord(
    String sourceName,
    String remoteName,
    List<String> remoteRelativeFilePaths,
    boolean overwrites,
    Instant timestamp) {
  public BackupStateRecord(
      final String sourceName,
      final String remoteName,
      final List<String> remoteRelativeFilePaths,
      final boolean overwrites) {
    this(sourceName, remoteName, remoteRelativeFilePaths, overwrites, Instant.now());
  }

  public BackupStateRecord(
      final String sourceName,
      final String remoteName,
      final List<String> remoteRelativeFilePaths) {
    this(sourceName, remoteName, remoteRelativeFilePaths, false);
  }
}
