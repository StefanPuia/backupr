package co.uk.stefanpuia.backupr.config.model.remote;

import jakarta.annotation.Nullable;

public interface ConfigRemote {
  @Nullable
  String getName();

  boolean isEnabled();

  RemoteType getType();
}
