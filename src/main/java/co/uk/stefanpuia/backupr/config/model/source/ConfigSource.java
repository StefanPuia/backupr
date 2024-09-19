package co.uk.stefanpuia.backupr.config.model.source;

import static co.uk.stefanpuia.backupr.config.model.BackuprConfig.VALID_IDENTIFIER_REGEX;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
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

  default void checkValid(BackuprConfig config) {
    if (!name().matches(VALID_IDENTIFIER_REGEX)) {
      throw new ConfigValidationException(
          "source '%s': name must match the following pattern: '%s'"
              .formatted(name(), VALID_IDENTIFIER_REGEX));
    }
  }

  Path getBasePath();
}
