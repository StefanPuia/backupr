package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.util.Set;

public interface RemoteHandler {
  RemoteHandler setDry(boolean dry);

  void upload(ConfigSource source, Set<File> files);
}
