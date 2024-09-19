package co.uk.stefanpuia.backupr.remote;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.util.List;

public interface RemoteHandler {
  void upload(ConfigSource source, List<File> files);
}
