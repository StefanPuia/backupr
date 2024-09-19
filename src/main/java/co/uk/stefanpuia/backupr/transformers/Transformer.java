package co.uk.stefanpuia.backupr.transformers;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.util.Set;

public interface Transformer {
  Set<File> transform(ConfigSource source, Set<File> files);
}
