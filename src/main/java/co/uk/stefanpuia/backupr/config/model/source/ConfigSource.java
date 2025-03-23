package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ConfigTransformerOptions;
import java.nio.file.Path;
import java.util.List;

public interface ConfigSource {
  String getName();

  boolean isEnabled();

  List<ConfigRemote> getRemotes();

  List<ConfigTransformerOptions> getTransformers();

  Path getBasePath();
}
