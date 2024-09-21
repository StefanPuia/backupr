package co.uk.stefanpuia.backupr.config.model.remote;

import static co.uk.stefanpuia.backupr.config.model.BackuprConfig.VALID_IDENTIFIER_REGEX;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @Type(value = LocalConfigRemote.class, name = "LOCAL"),
  @Type(value = AzureStorageConfigRemote.class, name = "AZURE_STORAGE"),
  @Type(value = GitConfigRemote.class, name = "GIT"),
})
public interface ConfigRemote {
  String name();

  RemoteType type();

  default void checkValid(BackuprConfig config) {
    if (!name().matches(VALID_IDENTIFIER_REGEX)) {
      throw new ConfigValidationException(
          "remote '%s': name must match the following pattern: '%s'"
              .formatted(name(), VALID_IDENTIFIER_REGEX));
    }
  }
}
