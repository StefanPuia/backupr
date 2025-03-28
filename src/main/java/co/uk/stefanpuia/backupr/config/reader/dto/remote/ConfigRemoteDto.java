package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import static co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto.VALID_IDENTIFIER_REGEX;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = LocalConfigRemoteDto.class, name = "LOCAL"),
  @JsonSubTypes.Type(value = AzureStorageConfigRemoteDto.class, name = "AZURE_STORAGE"),
  @JsonSubTypes.Type(value = GitConfigRemoteDto.class, name = "GIT"),
})
public interface ConfigRemoteDto {
  @Nullable
  @NotBlank
  @Pattern(regexp = VALID_IDENTIFIER_REGEX)
  String getName();

  @Nullable
  Boolean isDisabled();
}
