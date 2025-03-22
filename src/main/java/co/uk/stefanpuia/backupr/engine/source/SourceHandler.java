package co.uk.stefanpuia.backupr.engine.source;

import java.io.File;
import java.util.Set;

public interface SourceHandler {
  Set<File> getFiles();
}
