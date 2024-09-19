package co.uk.stefanpuia.backupr.remote;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.util.Set;

public interface RemoteHandler {
  void upload(ConfigSource source, Set<File> files);
}
