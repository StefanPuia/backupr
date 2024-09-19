package co.uk.stefanpuia.backupr.transformers;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.util.List;

public interface Transformer {
  List<File> transform(ConfigSource source, List<File> files);
}
