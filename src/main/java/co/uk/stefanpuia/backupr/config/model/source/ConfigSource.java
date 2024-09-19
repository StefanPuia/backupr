package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.nio.file.Path;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = LocalConfigSource.class)
@JsonSubTypes({
  @JsonSubTypes.Type(value = LocalConfigSource.class),
})
public interface ConfigSource {
  String name();

  List<String> remotes();

  List<ConfigRemote> remotes(final BackuprConfig config);

  List<SourceTransformer> transformers();

  void checkValid(BackuprConfig config);

  Path getBasePath();
}
