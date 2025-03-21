package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import java.nio.file.Path;
import java.util.List;

public interface ConfigSource {
  String getName();

  boolean isEnabled();

  List<ConfigRemote> getRemotes();

  List<SourceTransformer> getTransformers();

  Path getBasePath();
}
