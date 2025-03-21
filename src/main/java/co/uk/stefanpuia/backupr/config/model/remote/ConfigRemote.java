package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.RemoteType;

public interface ConfigRemote {
  String getName();

  boolean isEnabled();

  RemoteType getType();
}
