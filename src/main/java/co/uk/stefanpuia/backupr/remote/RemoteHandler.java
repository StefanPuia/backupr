package co.uk.stefanpuia.backupr.remote;

import java.io.File;
import java.util.List;

public interface RemoteHandler {
  void upload(List<File> files);
}
