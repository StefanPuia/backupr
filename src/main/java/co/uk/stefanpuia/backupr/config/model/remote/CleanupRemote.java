package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.cleanup.Cleanup;
import jakarta.annotation.Nullable;

public interface CleanupRemote {
  @Nullable
  String getName();

  @Nullable
  Cleanup getCleanup();
}
