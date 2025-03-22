package co.uk.stefanpuia.backupr.engine.transformers;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.util.Set;

public interface Transformer {
  Transformer setDry(boolean dry);

  Set<File> transform(ConfigSource source, Set<File> files);
}
