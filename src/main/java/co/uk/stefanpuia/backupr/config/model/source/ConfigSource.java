package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.cleanup.Cleanup;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.transformer.ConfigTransformerOptions;
import jakarta.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;

public interface ConfigSource {
  String getName();

  boolean isEnabled();

  @Nullable
  Cleanup getCleanup();

  List<ConfigRemote> getRemotes();

  List<ConfigTransformerOptions> getTransformers();

  Path getBasePath();
}
