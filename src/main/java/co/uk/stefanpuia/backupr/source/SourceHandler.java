package co.uk.stefanpuia.backupr.source;

import java.io.File;
import java.util.List;

public interface SourceHandler {
  List<File> getFiles();
}
