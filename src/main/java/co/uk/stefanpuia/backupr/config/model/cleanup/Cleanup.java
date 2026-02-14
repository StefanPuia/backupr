package co.uk.stefanpuia.backupr.config.model.cleanup;

import jakarta.annotation.Nullable;

public interface Cleanup {
  @Nullable
  String getName();

  boolean isEnabled();

  CleanupType getType();
}
