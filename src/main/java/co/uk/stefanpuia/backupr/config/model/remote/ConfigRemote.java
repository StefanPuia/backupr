package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import jakarta.annotation.Nullable;

public interface ConfigRemote {
  @Nullable
  String getName();

  boolean isEnabled();

  RemoteType getType();

  VariablesWrapper getVariables();
}
